package com.xuyuan.blog.admin.vo.misc;
import lombok.Data;
import java.io.Serializable;
@Data public class AdminNoticeVO implements Serializable { private Long id; private String content; private String createTime; private String updateTime; }
