package com.xuyuan.blog.admin.vo.comment;

import lombok.Data;

import java.io.Serializable;

@Data
public class AdminCommentVO implements Serializable {
    private Long id;
    private Integer targetType;
    private Long targetId;
    private String targetTitle;
    private String content;
    private String nickname;
    private String avatar;
    private String email;
    private String website;
    private Integer status;
    private String createTime;
    private String updateTime;
}
