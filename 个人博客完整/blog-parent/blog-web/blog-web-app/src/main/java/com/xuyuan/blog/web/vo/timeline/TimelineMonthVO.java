package com.xuyuan.blog.web.vo.timeline;

import com.xuyuan.blog.web.vo.article.ArticleNavVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 时间线归档(月)视图对象
 */
@Data
@Schema(description = "时间线归档-月(含当月文章列表)")
public class TimelineMonthVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 月份 */
    @Schema(description = "月份(yyyy-MM)", example = "2026-08")
    private String month;

    /** 该月文章列表 */
    @Schema(description = "该月文章列表(按发布时间倒序)")
    private List<ArticleNavVO> articles;
}
