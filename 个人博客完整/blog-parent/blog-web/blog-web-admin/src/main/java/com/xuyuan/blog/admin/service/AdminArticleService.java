package com.xuyuan.blog.admin.service;

import com.xuyuan.blog.admin.dto.ArticleCreateParam;
import com.xuyuan.blog.admin.dto.ArticleQueryParam;
import com.xuyuan.blog.admin.dto.ArticleStatusParam;
import com.xuyuan.blog.admin.dto.IdsParam;
import com.xuyuan.blog.admin.vo.article.AdminArticleVO;
import com.xuyuan.blog.admin.vo.article.ArticleDetailVO;
import com.xuyuan.blog.common.result.PageResult;

/**
 * 后台管理端-文章业务服务
 *
 * <p>任务书 M3 文章模块 7 个接口: 分页/新建/编辑/详情/批量删除/状态切换/草稿保存。</p>
 */
public interface AdminArticleService {

    /** 分页列表 (AdminArticleVO, 标签信息由 Service 层聚合回填) */
    PageResult<AdminArticleVO> pageList(ArticleQueryParam param);

    /** 详情 (含 content 正文 + tagIds 回显) */
    ArticleDetailVO detail(Long id);

    /** 新建文章 → 返回文章 id */
    Long create(ArticleCreateParam param);

    /** 编辑文章 (全量更新 + 标签关系重建) */
    void update(Long id, ArticleCreateParam param);

    /** 批量删除 → 逻辑删除 status = RECYCLE(3), 不物理删 */
    void deleteBatch(IdsParam param);

    /** 状态/置顶 变更 (ArticleStatusParam) */
    void changeStatus(Long id, ArticleStatusParam param);

    /**
     * 保存草稿
     *
     * <p>规则: 若 param 中能拿到主键(前端可能上送 id, 也可以不带), 或根据标题/作者能定位草稿, 则更新; 否则新建。
     * 为简化实现, 本版本按"id 存在则更新, 否则新建"处理, 因为草稿自动保存触发时通常带 id。</p>
     */
    Long saveDraft(Long id, ArticleCreateParam param);
}
