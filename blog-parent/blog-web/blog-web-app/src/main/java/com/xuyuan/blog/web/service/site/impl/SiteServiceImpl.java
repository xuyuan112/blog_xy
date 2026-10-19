package com.xuyuan.blog.web.service.site.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xuyuan.blog.model.entity.Notice;
import com.xuyuan.blog.model.entity.Schedule;
import com.xuyuan.blog.model.entity.Word;
import com.xuyuan.blog.web.mapper.SiteMapper;
import com.xuyuan.blog.web.service.site.SiteService;
import com.xuyuan.blog.web.vo.misc.NoticeVO;
import com.xuyuan.blog.web.vo.misc.WeatherVO;
import com.xuyuan.blog.web.vo.misc.WordVO;
import com.xuyuan.blog.web.vo.schedule.ScheduleVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SiteServiceImpl implements SiteService {

    private final SiteMapper siteMapper;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${amap.weather-key:}")
    private String amapWeatherKey;

    @Value("${amap.city-code:330100}")
    private String amapCityCode;

    @Override
    public NoticeVO notice() {
        Notice notice = siteMapper.latestNotice();
        NoticeVO result = new NoticeVO();
        if (notice != null) {
            BeanUtils.copyProperties(notice, result);
            result.setUpdatedAt(notice.getUpdateTime());
        }
        return result;
    }

    @Override
    public WordVO randomWord() {
        Word word = siteMapper.randomWord();
        WordVO result = new WordVO();
        if (word != null) {
            BeanUtils.copyProperties(word, result);
        }
        return result;
    }

    @Override
    public WeatherVO weather() {
        WeatherVO result = new WeatherVO();
        result.setCity("杭州");
        result.setWeather("数据暂不可用");
        result.setIcon("unknown");
        if (!StringUtils.hasText(amapWeatherKey)) {
            return result;
        }
        try {
            String url = "https://restapi.amap.com/v3/weather/weatherInfo?key="
                    + amapWeatherKey + "&city=" + amapCityCode + "&extensions=base";
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            JsonNode live = objectMapper.readTree(response.getBody()).path("lives").path(0);
            if (!live.isMissingNode()) {
                result.setCity(live.path("city").asText(result.getCity()));
                result.setTemp(live.path("temperature").asText(null));
                result.setWeather(live.path("weather").asText(result.getWeather()));
                result.setIcon(resolveWeatherIcon(result.getWeather()));
            }
        } catch (Exception ignored) {
            // 第三方天气服务不可用时返回降级数据，不影响首页其它模块。
        }
        return result;
    }

    private String resolveWeatherIcon(String weather) {
        if (weather == null) {
            return "unknown";
        }
        if (weather.contains("雨")) {
            return "rain";
        }
        if (weather.contains("雪")) {
            return "snow";
        }
        if (weather.contains("云") || weather.contains("阴")) {
            return "cloudy";
        }
        if (weather.contains("晴")) {
            return "sunny";
        }
        return "unknown";
    }

    @Override
    public List<ScheduleVO> schedules() {
        return siteMapper.schedules().stream().map(this::toScheduleVO).collect(Collectors.toList());
    }

    private ScheduleVO toScheduleVO(Schedule schedule) {
        ScheduleVO result = new ScheduleVO();
        BeanUtils.copyProperties(schedule, result);
        return result;
    }
}
