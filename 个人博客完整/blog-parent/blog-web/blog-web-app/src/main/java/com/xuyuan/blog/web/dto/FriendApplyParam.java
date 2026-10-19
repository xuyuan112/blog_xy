package com.xuyuan.blog.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * 友链申请请求参数(访客提交)
 */
@Data
public class FriendApplyParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 站点名称 */
    @NotBlank(message = "站点名称不能为空")
    private String name;

    /** 站点地址 */
    @NotBlank(message = "站点地址不能为空")
    private String url;

    /** 头像地址 */
    private String avatar;

    /** 站点描述 */
    private String description;

    /** 申请人邮箱 */
    @NotBlank(message = "邮箱不能为空")
    @jakarta.validation.constraints.Email(message = "邮箱格式不正确")
    private String email;

    /** 分组名 */
    private String group;
}
