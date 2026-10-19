package com.xuyuan.blog.web.controller.site;

import com.xuyuan.blog.web.service.music.NetEaseMusicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * 自建 Meting 协议端点（公开，无需登录）
 * <p>
 * 兼容 Meting API 响应约定，前端音乐播放器把 api 模板指向本端点即可零改动切换：
 * <ul>
 *   <li>type=playlist → JSON 数组 [{name, artist, url, pic, lrc}]（已过滤 VIP 不可播歌曲）</li>
 *   <li>type=url      → 302 重定向到真实音频 CDN 直链（VIP 歌返回 404）</li>
 *   <li>type=pic      → 302 重定向到封面图</li>
 *   <li>type=lrc      → text/plain 歌词文本</li>
 * </ul>
 * 注意：本端点返回原始 JSON 数组（非统一 R 包装），因为 Meting 协议消费方要求顶层是数组。
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "音乐代理", description = "自建 Meting 协议端点，代理网易云歌单/歌词/播放直链")
@RequestMapping("/xuyuan/site")
public class SiteMusicController {

    private final NetEaseMusicService netEaseMusicService;

    @GetMapping("/meting")
    @Operation(summary = "Meting 协议端点", description = "type=playlist|url|pic|lrc")
    public ResponseEntity<?> meting(@RequestParam String server,
                                    @RequestParam String type,
                                    @RequestParam String id) {
        // 当前仅支持网易云
        if (!"netease".equalsIgnoreCase(server)) {
            return ResponseEntity.badRequest().body(Map.of("error", "unsupported server: " + server));
        }
        switch (type.toLowerCase()) {
            case "playlist" -> {
                List<Map<String, Object>> playlist = netEaseMusicService.getPlaylist(id);
                if (playlist.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(Map.of("error", "playlist not found or no playable song"));
                }
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(playlist);
            }
            case "url" -> {
                String audioUrl = netEaseMusicService.resolveAudioUrl(id);
                if (audioUrl == null) {
                    // VIP 或不可播：让播放器走 error→自动跳下一首的容错链路
                    return notFound();
                }
                return ResponseEntity.status(HttpStatus.FOUND)
                        .header("Location", audioUrl)
                        .build();
            }
            case "pic" -> {
                String cover = netEaseMusicService.getCover(id);
                if (cover == null) {
                    return notFound();
                }
                return ResponseEntity.status(HttpStatus.FOUND)
                        .header("Location", cover)
                        .build();
            }
            case "lrc" -> {
                String lyric = netEaseMusicService.getLyric(id);
                if (lyric == null) {
                    return notFound();
                }
                return ResponseEntity.ok()
                        .contentType(new MediaType(MediaType.TEXT_PLAIN, StandardCharsets.UTF_8))
                        .body(lyric);
            }
            default -> {
                return ResponseEntity.badRequest().body(Map.of("error", "unsupported type: " + type));
            }
        }
    }

    private ResponseEntity<Void> notFound() {
        return ResponseEntity.notFound().build();
    }
}
