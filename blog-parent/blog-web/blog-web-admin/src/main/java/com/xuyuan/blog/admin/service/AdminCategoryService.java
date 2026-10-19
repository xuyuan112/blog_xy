package com.xuyuan.blog.admin.service;

import com.xuyuan.blog.admin.dto.CategoryCreateParam;
import com.xuyuan.blog.admin.vo.article.CategoryVO;

import java.util.List;

/** 后台-分类业务服务 */
public interface AdminCategoryService {

    /** 列表 (表一般几十条, 不分页, 按 sort 升序 + id 升序) */
    List<CategoryVO> listAll();

    /** 新增 */
    Long create(CategoryCreateParam param);

    /** 编辑 */
    void update(Long id, CategoryCreateParam param);

    /** 删除: 若分类下有文章 → 抛 ADMIN_CATEGORY_HAS_ARTICLES(313) */
    void delete(Long id);
}
