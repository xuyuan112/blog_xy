package com.xuyuan.blog.web.vo.friend;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 友链视图对象
 */
@Data
@Schema(description = "友链(前台展示用)")
public class FriendVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 友链 ID */
    @Schema(description = "友链 ID", example = "1")
    private Long id;

    /** 站点名称 */
    @Schema(description = "站点名称", example = "许袁的博客")
    private String name;

    /** 头像地址 */
    @Schema(description = "头像地址(URL)", example = "https://example.com/favicon.png")
    private String avatar;

    /** 站点描述 */
    @Schema(description = "站点描述", example = "分享技术与人文创作")
    private String description;

    /** 站点地址 */
    @Schema(description = "站点地址(URL)", example = "https://example.com")
    private String url;

    /** 内部分组字段，由服务层聚合后不直接要求前端展示。 */
    @Schema(description = "友链分组名称", hidden = true)
    private String groupName;
}
