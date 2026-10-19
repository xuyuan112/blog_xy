package com.xuyuan.blog.web.vo.friend;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 友链分组视图对象
 */
@Data
@Schema(description = "友链分组(按 groupName 聚合)")
public class FriendGroupVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 分组名 */
    @Schema(description = "分组名", example = "技术博客")
    private String name;

    /** 分组下的友链 */
    @Schema(description = "分组下的友链列表")
    private List<FriendVO> friends;
}
