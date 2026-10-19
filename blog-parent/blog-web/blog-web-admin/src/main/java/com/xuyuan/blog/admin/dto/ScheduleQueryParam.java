package com.xuyuan.blog.admin.dto;
import lombok.Data;import java.io.Serializable;
@Data public class ScheduleQueryParam implements Serializable { private long page=1; private long pageSize=10; private String type; }
