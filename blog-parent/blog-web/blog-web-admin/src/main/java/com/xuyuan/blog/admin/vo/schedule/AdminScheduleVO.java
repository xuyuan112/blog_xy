package com.xuyuan.blog.admin.vo.schedule;
import lombok.Data;import java.io.Serializable;
@Data public class AdminScheduleVO implements Serializable { private Long id;private String title;private String targetTime;private String type;private String createTime; }
