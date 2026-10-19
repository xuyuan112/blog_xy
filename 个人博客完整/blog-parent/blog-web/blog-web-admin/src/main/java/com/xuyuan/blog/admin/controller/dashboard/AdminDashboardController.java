package com.xuyuan.blog.admin.controller.dashboard;

import com.xuyuan.blog.admin.service.AdminDashboardService;
import com.xuyuan.blog.admin.vo.dashboard.*;
import com.xuyuan.blog.common.result.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Tag(name = "后台仪表盘统计")
@RestController
@RequestMapping("/xuyuan/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {
    private final AdminDashboardService service;
    @GetMapping("/stats")
    @Operation(summary = "仪表盘总览统计")
    public R<DashboardStatsVO> stats() {
        return R.ok(service.stats());
    }

    @GetMapping("/trend")
    @Operation(summary = "访问趋势统计")
    public R<List<TrendPointVO>> trend(
            @RequestParam(defaultValue = "30") int days) {
        return R.ok(service.trend(days));
    }
}
