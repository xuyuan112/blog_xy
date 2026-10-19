package com.xuyuan.blog.admin.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class CommentQueryParam implements Serializable {
    private static final long serialVersionUID = 1L;
    private long page = 1;
    private long pageSize = 10;
    private Integer targetType;
    private Integer status;
}
