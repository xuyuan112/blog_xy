package com.xuyuan.blog.admin.service;

import com.xuyuan.blog.admin.dto.MomentCreateParam;
import com.xuyuan.blog.admin.dto.MomentQueryParam;
import com.xuyuan.blog.admin.vo.moment.AdminMomentVO;
import com.xuyuan.blog.common.result.PageResult;

public interface AdminMomentService {
    PageResult<AdminMomentVO> pageList(MomentQueryParam param);
    Long create(MomentCreateParam param);
    void update(Long id, MomentCreateParam param);
    void delete(Long id);
}
