package com.xuyuan.blog.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 发表评论请求参数
 */
@Data
public class CommentCreateParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 目标类型: 1 文章, 2 说说 */
    @NotNull(message = "目标类型不能为空")
    private Integer targetType;

    /** 目标 ID */
    @NotNull(message = "目标 ID 不能为空")
    private Long targetId;

    /** 评论内容 */
    @NotBlank(message = "评论内容不能为空")
    private String content;

    /** 评论者昵称 */
    @NotBlank(message = "昵称不能为空")
    private String nickname;

    /** 评论者邮箱 */
    private String email;

    /** 评论者站点 */
    private String website;
}
