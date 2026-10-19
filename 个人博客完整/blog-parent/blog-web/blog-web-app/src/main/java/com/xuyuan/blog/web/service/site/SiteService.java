package com.xuyuan.blog.web.service.site;
import com.xuyuan.blog.web.vo.misc.*; import com.xuyuan.blog.web.vo.schedule.ScheduleVO; import java.util.List;
public interface SiteService { NoticeVO notice(); WordVO randomWord(); WeatherVO weather(); List<ScheduleVO> schedules(); }
