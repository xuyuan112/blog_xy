package com.xuyuan.blog.admin.service;

import com.xuyuan.blog.admin.dto.TagCreateParam;
import com.xuyuan.blog.admin.vo.article.TagVO;

import java.util.List;

/** 后台-标签业务服务 */
public interface AdminTagService {

    /** 列表 (不分页, 按 id 升序, 含文章数统计) */
    List<TagVO> listAll();

    /** 新增 */
    Long create(TagCreateParam param);

    /** 编辑 */
    void update(Long id, TagCreateParam param);

    /** 删除: 先删 t_article_tag 中该标签的全部关联, 再删 t_tag */
    void delete(Long id);
}
