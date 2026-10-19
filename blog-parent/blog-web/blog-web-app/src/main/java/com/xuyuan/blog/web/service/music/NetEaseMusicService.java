package com.xuyuan.blog.web.service.music;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Semaphore;

/**
 * 网易云音乐代理服务（自建 Meting 协议后端）
 * <p>
 * 职责：
 * 1. 调用网易云公开接口获取歌单/歌曲详情/歌词（伪装浏览器 UA + Referer）
 * 2. 解析外链播放地址（outer/url 重定向链），并用 Range 探测甄别 VIP 不可播歌曲
 * Redis Key 规范：blog:site:music:{业务}:{id}
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NetEaseMusicService {

    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    /** 拼接返回给前端的 Meting 接口绝对地址（部署到服务器后改成公网域名） */
    @Value("${blog.music.public-base-url:http://localhost:8081}")
    private String publicBaseUrl;

    private static final String UA = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/121.0.0.0 Safari/537.36";
    private static final String NETEASE_PLAYLIST_V6 = "https://music.163.com/api/v6/playlist/detail?id=%s&n=1000";
    private static final String NETEASE_SONG_DETAIL = "https://music.163.com/api/song/detail/?id=%s&ids=%%5B%s%%5D";
    private static final String NETEASE_LYRIC = "https://music.163.com/api/song/lyric?id=%s&lv=-1&kv=-1&tv=-1";
    private static final String NETEASE_OUTER_URL = "https://music.163.com/song/media/outer/url?id=%s.mp3";

    /** 真实音频文件最小字节数，小于该值视为 VIP 占位音频（外链对 VIP 歌返回空/极小文件） */
    private static final long MIN_AUDIO_BYTES = 100_000L;
    /**
     * 探测 outer/url 的最大并发：网易云对未登录 IP 的批量外链请求有频率限制，
     * 全并发会触发限流，14 首歌中一半会被误判 VIP。降到 3 保证安全同时不阻塞太久。
     */
    private static final int PROBE_MAX_CONCURRENCY = 3;

    private static final String KEY_PLAYLIST = "blog:site:music:playlist:";
    private static final String KEY_SONG = "blog:site:music:song:";
    private static final String KEY_LRC = "blog:site:music:lrc:";
    private static final String KEY_AUDIO = "blog:site:music:audio:";
    private static final String KEY_BAD = "blog:site:music:bad:";

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .followRedirects(HttpClient.Redirect.NEVER)
            .build();
    /** 全局探测并发闸门，保证同时最多 N 个 outer/url 请求 */
    private final Semaphore probeSemaphore = new Semaphore(PROBE_MAX_CONCURRENCY);

    /** 探测结果三态：成功 / 明确不可播（VIP/版权失效） / 网络问题（不限速重试） */
    private enum ProbeResult { OK, VIP_OR_INVALID, NETWORK_ERROR }
    private record ProbeOutcome(ProbeResult result, String url) {}

    // ────────────────────────── 对外方法 ──────────────────────────

    /**
     * 获取歌单（Meting 协议格式）
     * <p>
     * 注意：本方法不预探测每首是否可播——这样 14 首歌不会因网易云 outer/url 的并发限流
     * 被批量误判为 VIP。可播性判定下推到 resolveAudioUrl，在播放器按需访问 /meting?type=url
     * 时单首单次完成，VIP/失效歌曲返回 404，由前端 onerror 链路自动跳下一首。
     *
     * @return 每项 {name, artist, url, pic, lrc}；拉取失败返回空列表
     */
    public List<Map<String, Object>> getPlaylist(String playlistId) {
        String cacheKey = KEY_PLAYLIST + playlistId;
        try {
            String cached = stringRedisTemplate.opsForValue().get(cacheKey);
            if (cached != null) {
                return objectMapper.readValue(cached,
                        objectMapper.getTypeFactory().constructCollectionType(List.class, Map.class));
            }
        } catch (Exception e) {
            log.warn("[Music] 歌单缓存读取失败 key={}", cacheKey, e);
        }

        List<Map<String, Object>> result = fetchPlaylistAll(playlistId);
        if (!result.isEmpty()) {
            try {
                // 缓存 5 分钟：网易云歌单偶尔调整，保证刷新及时；同时限制历史错误结果持续时间
                stringRedisTemplate.opsForValue().set(cacheKey, objectMapper.writeValueAsString(result), Duration.ofMinutes(5));
            } catch (Exception e) {
                log.warn("[Music] 歌单缓存写入失败 key={}", cacheKey, e);
            }
        }
        return result;
    }

    /** 解析某首歌可用的播放直链；返回 null 让播放器走 404→自动跳下一首的容错链路 */
    public String resolveAudioUrl(String songId) {
        String ok = stringRedisTemplate.opsForValue().get(KEY_AUDIO + songId);
        if (ok != null) {
            return ok;
        }
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(KEY_BAD + songId))) {
            return null;
        }

        // 全局并发闸门：网易云 outer/url 对未登录 IP 限流，全并发会导致大量"探测失败=误判 VIP"
        try {
            probeSemaphore.acquire();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
        try {
            ProbeOutcome outcome = probeOuterUrl(songId);
            switch (outcome.result()) {
                case OK -> {
                    // CDN 时效链接，缓存 30 分钟
                    stringRedisTemplate.opsForValue().set(KEY_AUDIO + songId, outcome.url(),
                            Duration.ofMinutes(30));
                    return outcome.url();
                }
                case VIP_OR_INVALID -> {
                    // 网易云明确说不可播（重定向到非音频 / content-range 总大小 < 100KB 占位音频）
                    // 负缓存 30 分钟，期间不再重试
                    stringRedisTemplate.opsForValue().set(KEY_BAD + songId, "1",
                            Duration.ofMinutes(30));
                    return null;
                }
                case NETWORK_ERROR -> {
                    // 探测本身失败（超时 / 连接异常 / 5xx）：不缓存，让下一首/下一次按需重试
                    // 这是修关键误杀的核心：网络抖动不再让"非VIP"歌被永久锁定
                    log.info("[Music] 探测失败但疑为网络问题 id={}，下次再试", songId);
                    return null;
                }
            }
        } finally {
            probeSemaphore.release();
        }
        return null;
    }

    /** 获取歌词 LRC 文本；无歌词返回 null */
    public String getLyric(String songId) {
        String cacheKey = KEY_LRC + songId;
        String cached = stringRedisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return "none".equals(cached) ? null : cached;
        }
        String lyric = null;
        try {
            JsonNode root = httpGetJson(String.format(NETEASE_LYRIC, songId));
            if (root != null && root.path("lrc").has("lyric")) {
                String text = root.path("lrc").path("lyric").asText("").trim();
                // 网易云对无歌词歌曲返回纯音乐占位文本
                if (!text.isEmpty() && !text.contains("纯音乐，请欣赏")) {
                    lyric = text;
                }
            }
        } catch (Exception e) {
            log.warn("[Music] 歌词获取失败 id={}", songId, e);
        }
        // 无歌词也缓存，防止反复请求
        stringRedisTemplate.opsForValue().set(cacheKey, lyric == null ? "none" : lyric, Duration.ofDays(7));
        return lyric;
    }

    /** 获取歌曲封面图 URL */
    public String getCover(String songId) {
        Map<String, String> detail = getSongDetail(songId);
        return detail == null ? null : detail.get("cover");
    }

    // ────────────────────────── 内部实现 ──────────────────────────

    /**
     * 拉取歌单详情，对每首歌做受限并发的可播性探测，仅把确实可播的返回。
     * <p>
     * 网易云 v6 API 的 playlist.tracks 只返回前 10 首详情，playlist.trackIds 包含全部 ID，
     * 缺失的歌曲走 song/detail 接口补全，无法补到的用占位（id 仍保留）。
     * <p>
     * 三态判定（与 probeOuterUrl 对齐）：
     * <ul>
     *   <li>OK → 加入 playlist，并把 CDN 直链写 KEY_AUDIO 缓存 30 min</li>
     *   <li>VIP_OR_INVALID → 不加入 playlist（写 KEY_BAD 缓存 30 min，下次直接跳过探测）</li>
     *   <li>NETWORK_ERROR → 保守策略：仍加入 playlist，让播放器 onerror 跳过，避免漏歌</li>
     * </ul>
     * 限流原因：14 首歌全并发会被网易云限流；用全局 Semaphore 闸门把并发降到 3。
     * 命中 KEY_BAD 的歌本次不再探测（已知不可播），降低不必要的网易云请求。
     * 命中 KEY_AUDIO 的歌本次不再探测（已知可播，直接复用）。
     */
    private List<Map<String, Object>> fetchPlaylistAll(String playlistId) {
        JsonNode root = httpGetJson(String.format(NETEASE_PLAYLIST_V6, playlistId));
        JsonNode playlistNode = root == null ? null : root.path("playlist");
        if (playlistNode.isMissingNode()) {
            log.warn("[Music] 歌单拉取失败或为空 id={}", playlistId);
            return List.of();
        }

        JsonNode trackIds = playlistNode.path("trackIds");
        JsonNode tracks = playlistNode.path("tracks");
        if (!trackIds.isArray() || trackIds.isEmpty()) {
            log.warn("[Music] 歌单 trackIds 为空 id={}", playlistId);
            return List.of();
        }

        // 1. 收齐所有歌曲的 metadata
        Map<String, Map<String, String>> songMap = new LinkedHashMap<>();
        if (tracks.isArray()) {
            for (JsonNode t : tracks) {
                songMap.put(t.path("id").asText(), extractSong(t));
            }
        }
        for (JsonNode tid : trackIds) {
            String sid = tid.path("id").asText();
            if (!songMap.containsKey(sid)) {
                Map<String, String> detail = getSongDetail(sid);
                songMap.put(sid, detail != null ? detail : placeholderSong(sid));
            }
        }
        // 按 trackIds 顺序保持歌单原顺序
        List<Map<String, String>> orderedSongs = new ArrayList<>();
        for (JsonNode tid : trackIds) {
            Map<String, String> s = songMap.get(tid.path("id").asText());
            if (s != null) orderedSongs.add(s);
        }
        int total = orderedSongs.size();

        // 2. 受限并发探测
        Map<String, CompletableFuture<ProbeOutcome>> futures = new LinkedHashMap<>();
        for (Map<String, String> song : orderedSongs) {
            String sid = song.get("id");
            futures.put(sid, CompletableFuture.supplyAsync(() -> probeOneForPlaylist(sid)));
        }
        CompletableFuture.allOf(futures.values().toArray(new CompletableFuture[0])).join();

        // 3. 按原顺序输出：OK 入清单，VIP_OR_INVALID 写负缓存丢弃，NETWORK_ERROR 保守保留
        List<Map<String, Object>> playlist = new ArrayList<>();
        int nOk = 0, nVip = 0, nNet = 0;
        for (Map<String, String> song : orderedSongs) {
            String sid = song.get("id");
            ProbeOutcome outcome = futures.get(sid).join();
            switch (outcome.result()) {
                case OK -> {
                    if (outcome.url() != null) {
                        try {
                            stringRedisTemplate.opsForValue().set(KEY_AUDIO + sid, outcome.url(),
                                    Duration.ofMinutes(30));
                        } catch (Exception ignored) {
                        }
                    }
                    playlist.add(toMetingItem(song));
                    nOk++;
                }
                case VIP_OR_INVALID -> {
                    try {
                        stringRedisTemplate.opsForValue().set(KEY_BAD + sid, "1",
                                Duration.ofMinutes(30));
                    } catch (Exception ignored) {
                    }
                    log.info("[Music] 过滤不可播: {} - {}", song.get("name"), song.get("artist"));
                    nVip++;
                }
                case NETWORK_ERROR -> {
                    // 探测失败时不缓存也不丢弃：留给播放器按需重试，宁愿偶尔 onerror 跳也不漏歌
                    playlist.add(toMetingItem(song));
                    log.info("[Music] 探测网络异常 id={}，先保留", sid);
                    nNet++;
                }
            }
        }
        log.info("[Music] 歌单 {} 共 {} 首 → ok={} / vip={} / net={}, playlist 最终 {} 首",
                playlistId, total, nOk, nVip, nNet, playlist.size());
        return playlist;
    }

    /**
     * 单首歌探测：先看 KEY_BAD 跳过已知不可播，再看 KEY_AUDIO 复用已知可播，
     * 都没有就走 probeOuterUrl 真实探测；全程走全局探测并发闸门。
     */
    private ProbeOutcome probeOneForPlaylist(String songId) {
        try {
            probeSemaphore.acquire();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new ProbeOutcome(ProbeResult.NETWORK_ERROR, null);
        }
        try {
            if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(KEY_BAD + songId))) {
                return new ProbeOutcome(ProbeResult.VIP_OR_INVALID, null);
            }
            String cachedAudio = null;
            try {
                cachedAudio = stringRedisTemplate.opsForValue().get(KEY_AUDIO + songId);
            } catch (Exception ignored) {
            }
            if (cachedAudio != null && !cachedAudio.isEmpty()) {
                return new ProbeOutcome(ProbeResult.OK, cachedAudio);
            }
            return probeOuterUrl(songId);
        } finally {
            probeSemaphore.release();
        }
    }

    private Map<String, String> extractSong(JsonNode t) {
        Map<String, String> song = new LinkedHashMap<>();
        song.put("id", t.path("id").asText());
        song.put("name", t.path("name").asText("未知歌曲"));
        // v6 API 用 ar 而非 artists，al 而非 album
        song.put("artist", t.path("ar").path(0).path("name").asText(
                t.path("artists").path(0).path("name").asText("未知歌手")));
        song.put("cover", t.path("al").path("picUrl").asText(
                t.path("album").path("picUrl").asText("")));
        return song;
    }

    private Map<String, String> placeholderSong(String sid) {
        Map<String, String> ph = new LinkedHashMap<>();
        ph.put("id", sid);
        ph.put("name", "未知歌曲");
        ph.put("artist", "未知歌手");
        ph.put("cover", "");
        return ph;
    }

    /** 组装 Meting 协议条目：url/lrc 指回本代理端点，浏览器播放时再 302 到真实 CDN */
    private Map<String, Object> toMetingItem(Map<String, String> song) {
        String id = song.get("id");
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("name", song.get("name"));
        item.put("artist", song.get("artist"));
        item.put("url", publicBaseUrl + "/xuyuan/site/meting?server=netease&type=url&id=" + id);
        item.put("pic", song.get("cover"));
        item.put("lrc", publicBaseUrl + "/xuyuan/site/meting?server=netease&type=lrc&id=" + id);
        return item;
    }

    /** 歌曲详情（名称/歌手/封面），带 7 天缓存 */
    private Map<String, String> getSongDetail(String songId) {
        String cacheKey = KEY_SONG + songId;
        try {
            String cached = stringRedisTemplate.opsForValue().get(cacheKey);
            if (cached != null) {
                return objectMapper.readValue(cached, objectMapper.getTypeFactory().constructMapType(LinkedHashMap.class, String.class, String.class));
            }
        } catch (Exception ignored) {
        }
        Map<String, String> detail = null;
        try {
            JsonNode root = httpGetJson(String.format(NETEASE_SONG_DETAIL, songId, songId));
            JsonNode song = root == null ? null : root.path("songs").path(0);
            if (song != null && !song.isMissingNode()) {
                detail = new LinkedHashMap<>();
                detail.put("id", songId);
                detail.put("name", song.path("name").asText("未知歌曲"));
                detail.put("artist", song.path("artists").path(0).path("name").asText("未知歌手"));
                detail.put("cover", song.path("album").path("picUrl").asText(""));
            }
        } catch (Exception e) {
            log.warn("[Music] 歌曲详情获取失败 id={}", songId, e);
        }
        if (detail != null) {
            try {
                stringRedisTemplate.opsForValue().set(cacheKey, objectMapper.writeValueAsString(detail), Duration.ofDays(7));
            } catch (Exception ignored) {
            }
        }
        return detail;
    }

    /**
     * 探测 outer/url 重定向链。
     * <p>
     * 返回三态：
     * <ul>
     *   <li>OK：最终 200/206，content-type 音频，探测 size 充足 — 同时携带可重用的 CDN 直链</li>
     *   <li>VIP_OR_INVALID：找到了 final URL 但响应不是音频或 size 是占位（< 100KB） — 真正不可播</li>
     *   <li>NETWORK_ERROR：探测本身异常（超时/连接失败/5xx 等） — 不可判定，需要下次重试</li>
     * </ul>
     * 这种区分避免过去"探测失败 = 误判 VIP = 永久负缓存"导致正常歌曲消失的问题。
     */
    private ProbeOutcome probeOuterUrl(String songId) {
        try {
            URI uri = URI.create(String.format(NETEASE_OUTER_URL, songId));
            boolean networkIssue = false;
            for (int hop = 0; hop < 3 && uri != null; hop++) {
                HttpRequest request;
                try {
                    request = HttpRequest.newBuilder(uri)
                            .timeout(Duration.ofSeconds(6))
                            .header("User-Agent", UA)
                            .header("Referer", "https://music.163.com/")
                            .header("Range", "bytes=0-1")
                            .GET()
                            .build();
                } catch (Exception e) {
                    return new ProbeOutcome(ProbeResult.NETWORK_ERROR, null);
                }
                HttpResponse<Void> resp;
                try {
                    resp = httpClient.send(request, HttpResponse.BodyHandlers.discarding());
                } catch (java.io.IOException e) {
                    // SocketTimeoutException / ConnectException / HttpConnectTimeoutException 都是 IOException 子类
                    // 这类异常是网络抖动，重试即可（不写负缓存）
                    log.debug("[Music] probe io issue id={} hop={} {}", songId, hop, e.getClass().getSimpleName());
                    return new ProbeOutcome(ProbeResult.NETWORK_ERROR, null);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return new ProbeOutcome(ProbeResult.NETWORK_ERROR, null);
                } catch (Exception e) {
                    return new ProbeOutcome(ProbeResult.NETWORK_ERROR, null);
                }

                if (resp.statusCode() >= 300 && resp.statusCode() < 400) {
                    uri = resp.headers().firstValue("location").map(URI::create).orElse(null);
                    continue;
                }
                // 5xx、429 等都视为网络问题，不负缓存，让下次重试
                if (resp.statusCode() >= 500 || resp.statusCode() == 429) {
                    return new ProbeOutcome(ProbeResult.NETWORK_ERROR, null);
                }
                // 带 Range 头时 CDN 返回 206 Partial Content，200 为未分段响应，两者均视为有效
                if (resp.statusCode() != 200 && resp.statusCode() != 206) {
                    // 403/404 — 多半是版权/区域受限或失效 = 真正不可播
                    return new ProbeOutcome(ProbeResult.VIP_OR_INVALID, null);
                }
                String contentType = resp.headers().firstValue("content-type").orElse("").toLowerCase();
                if (!contentType.contains("audio") && !contentType.contains("octet-stream")) {
                    // 重定向到了 JSON 错误页或 HTML 错误页 — 真正不可播
                    return new ProbeOutcome(ProbeResult.VIP_OR_INVALID, null);
                }
                // content-range: bytes 0-1/4526378 → 解析总大小
                long total = resp.headers().firstValue("content-range")
                        .map(v -> {
                            int idx = v.lastIndexOf('/');
                            try {
                                return idx >= 0 ? Long.parseLong(v.substring(idx + 1)) : -1L;
                            } catch (NumberFormatException e) {
                                return -1L;
                            }
                        })
                        .orElse(-1L);
                if (total >= 0 && total < MIN_AUDIO_BYTES) {
                    // 明确小于 100KB 的占位音频 — 真正 VIP
                    return new ProbeOutcome(ProbeResult.VIP_OR_INVALID, null);
                }
                // 通过！
                return new ProbeOutcome(ProbeResult.OK, uri.toString());
            }
        } catch (Exception e) {
            log.warn("[Music] 播放地址探测异常 id={}", songId, e);
        }
        return new ProbeOutcome(ProbeResult.NETWORK_ERROR, null);
    }

    /** 带浏览器伪装头的 GET，返回 JSON；失败返回 null */
    private JsonNode httpGetJson(String url) {
        try {
            HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                    .timeout(Duration.ofSeconds(6))
                    .header("User-Agent", UA)
                    .header("Referer", "https://music.163.com/")
                    .GET()
                    .build();
            HttpResponse<String> resp = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() == 200) {
                return objectMapper.readTree(resp.body());
            }
            log.warn("[Music] 网易云接口非 200: {} status={}", url, resp.statusCode());
        } catch (Exception e) {
            log.warn("[Music] 网易云接口请求失败: {}", url, e);
        }
        return null;
    }
}
