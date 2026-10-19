package com.xuyuan.blog.admin.service;

import com.xuyuan.blog.admin.vo.dashboard.DashboardStatsVO;
import com.xuyuan.blog.admin.vo.dashboard.TrendPointVO;
import java.util.List;

public interface AdminDashboardService {
    DashboardStatsVO stats();
    List<TrendPointVO> trend(int days);
}
