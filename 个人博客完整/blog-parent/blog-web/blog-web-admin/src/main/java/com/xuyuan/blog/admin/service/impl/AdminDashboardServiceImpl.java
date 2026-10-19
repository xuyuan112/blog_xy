package com.xuyuan.blog.admin.service.impl;

import com.xuyuan.blog.admin.mapper.DashboardMapper;
import com.xuyuan.blog.admin.service.AdminDashboardService;
import com.xuyuan.blog.admin.vo.dashboard.DashboardStatsVO;
import com.xuyuan.blog.admin.vo.dashboard.TrendPointVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminDashboardServiceImpl implements AdminDashboardService {
    private final DashboardMapper mapper;
    @Override public DashboardStatsVO stats() { DashboardStatsVO v=new DashboardStatsVO();v.setArticleCount(mapper.articleCount());v.setMomentCount(mapper.momentCount());v.setGalleryCount(mapper.galleryCount());v.setPhotoCount(mapper.photoCount());v.setFriendCount(mapper.friendCount());v.setCommentCount(mapper.commentCount());v.setTotalViewCount(mapper.totalViewCount());v.setPendingFriendCount(mapper.pendingFriendCount());v.setPendingCommentCount(mapper.pendingCommentCount());return v; }
    @Override public List<TrendPointVO> trend(int days) { int n=Math.max(1,Math.min(days,90));LocalDate today=LocalDate.now();List<TrendPointVO> list=new ArrayList<>(n);for(int i=n-1;i>=0;i--){LocalDate d=today.minusDays(i);LocalDateTime s=d.atStartOfDay(),e=d.plusDays(1).atStartOfDay();TrendPointVO v=new TrendPointVO();v.setDate(d.toString());v.setViewCount(mapper.viewCount(s,e));v.setCommentCount(mapper.trendCommentCount(s,e));list.add(v);}return list; }
}

