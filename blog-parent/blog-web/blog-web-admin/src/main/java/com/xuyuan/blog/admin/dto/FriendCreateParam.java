package com.xuyuan.blog.admin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * 友链新增/编辑请求参数(后台)
 */
@Data
public class FriendCreateParam implements Serializable {

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

    /** 分组名 */
    private String group;

    /** 审核状态: 0 待审核, 1 已通过, 2 已拒绝 */
    private Integer status;
}
