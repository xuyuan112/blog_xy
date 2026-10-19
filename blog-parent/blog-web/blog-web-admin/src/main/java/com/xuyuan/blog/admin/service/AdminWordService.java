package com.xuyuan.blog.admin.service;

import com.xuyuan.blog.admin.dto.WordCreateParam;
import com.xuyuan.blog.admin.vo.misc.AdminWordVO;
import com.xuyuan.blog.common.result.PageResult;

public interface AdminWordService {
    PageResult<AdminWordVO> pageList(long page, long size);
    Long create(WordCreateParam param);
    void update(Long id, WordCreateParam param);
    void delete(Long id);
}
