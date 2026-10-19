package com.xuyuan.blog.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xuyuan.blog.common.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 友情链接实体 (t_friend)
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_friend")
@Schema(description = "友情链接实体")
public class Friend extends BaseEntity {

    /** 站点名称 */
    @Schema(description = "站点名称", example = "许袁的博客", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    /** 站点地址 */
    @Schema(description = "站点地址(URL)", example = "https://example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String url;

    /** 头像地址 */
    @Schema(description = "头像地址(URL)", example = "https://example.com/favicon.png")
    private String avatar;

    /** 站点描述 */
    @Schema(description = "站点描述", example = "分享技术与人文创作")
    private String description;

    /** 分组名 */
    @Schema(description = "分组名", example = "技术博客")
    private String groupName;

    /** 申请人邮箱(用于通知) */
    @Schema(description = "申请人邮箱(用于审核通知)", example = "admin@example.com")
    private String email;

    /** 审核状态: 0 待审核, 1 已通过, 2 已拒绝 */
    @Schema(description = "审核状态(0 待审核, 1 已通过, 2 已拒绝)", example = "0")
    private Integer status;

    /** 排序值 */
    @Schema(description = "排序值(越小越靠前)", example = "1")
    private Integer sort;
}
