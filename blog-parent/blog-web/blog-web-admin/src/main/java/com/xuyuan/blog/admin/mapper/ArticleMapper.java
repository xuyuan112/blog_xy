package com.xuyuan.blog.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuyuan.blog.admin.dto.ArticleQueryParam;
import com.xuyuan.blog.admin.vo.article.AdminArticleVO;
import com.xuyuan.blog.model.entity.Article;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 文章 Mapper
 *
 * <p>继承 MyBatis-Plus BaseMapper 提供基础 CRUD;
 * 自定义 {@link #pageAdminList} 做 admin 端分页(关联 category_name 字段)。</p>
 */
@Mapper
public interface ArticleMapper extends BaseMapper<Article> {

    /**
     * 后台管理端文章分页列表: 关联 t_category 表查 category_name,
     * 支持 status/type/keyword(title+summary) 三维筛选, 按 isTop desc + published_at desc + id desc 排序。
     */
    IPage<AdminArticleVO> pageAdminList(Page<AdminArticleVO> page,
                                        @Param("param") ArticleQueryParam param);
}
