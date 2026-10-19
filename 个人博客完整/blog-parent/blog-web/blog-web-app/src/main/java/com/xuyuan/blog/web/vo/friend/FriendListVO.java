package com.xuyuan.blog.web.vo.friend;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 友链列表视图对象
 */
@Data
@Schema(description = "友链列表(按分组聚合)")
public class FriendListVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 友链总数 */
    @Schema(description = "友链总数(所有分组累计)", example = "10")
    private Long total;

    /** 分组列表 */
    @Schema(description = "分组列表")
    private List<FriendGroupVO> groups;
}
