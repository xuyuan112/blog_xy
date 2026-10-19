package com.xuyuan.blog.admin.vo.dashboard;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 仪表盘统计视图对象
 */
@Data
@Schema(description = "仪表盘统计数据(首页统计卡片)")
public class DashboardStatsVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 文章数 */
    @Schema(description = "文章总数", example = "42")
    private Long articleCount;

    /** 说个数 */
    @Schema(description = "说说总数", example = "15")
    private Long momentCount;

    /** 相册数 */
    @Schema(description = "相册总数", example = "5")
    private Long galleryCount;

    /** 照片数 */
    @Schema(description = "照片总数", example = "128")
    private Long photoCount;

    /** 友链数 */
    @Schema(description = "友链总数", example = "10")
    private Long friendCount;

    /** 评论数 */
    @Schema(description = "评论总数", example = "256")
    private Long commentCount;

    /** 总阅读量 */
    @Schema(description = "总阅读量", example = "10240")
    private Long totalViewCount;

    /** 待审核友链数 */
    @Schema(description = "待审核友链数", example = "3")
    private Long pendingFriendCount;

    /** 待审核评论数 */
    @Schema(description = "待审核评论数", example = "8")
    private Long pendingCommentCount;
}
