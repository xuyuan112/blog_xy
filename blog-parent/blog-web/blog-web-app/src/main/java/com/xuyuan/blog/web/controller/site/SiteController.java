package com.xuyuan.blog.web.controller.site;

import com.xuyuan.blog.common.result.R;
import com.xuyuan.blog.web.service.site.SiteService;
import com.xuyuan.blog.web.vo.misc.NoticeVO;
import com.xuyuan.blog.web.vo.misc.WeatherVO;
import com.xuyuan.blog.web.vo.misc.WordVO;
import com.xuyuan.blog.web.vo.schedule.ScheduleVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "首页与站点服务", description = "首页侧栏公告、一言、天气和日程")
@RequestMapping("/xuyuan/site")
public class SiteController {

    private final SiteService siteService;

    @GetMapping("/notice")
    @Operation(summary = "站长公告")
    public R<NoticeVO> notice() {
        return R.ok(siteService.notice());
    }

    @GetMapping("/words/random")
    @Operation(summary = "随机一言")
    public R<WordVO> randomWord() {
        return R.ok(siteService.randomWord());
    }

    @GetMapping("/weather")
    @Operation(summary = "天气信息")
    public R<WeatherVO> weather() {
        return R.ok(siteService.weather());
    }

    @GetMapping("/schedules")
    @Operation(summary = "日程与倒计时")
    public R<List<ScheduleVO>> schedules() {
        return R.ok(siteService.schedules());
    }
}
