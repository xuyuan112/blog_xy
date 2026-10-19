package com.xuyuan.blog.web.service.moment;

import com.xuyuan.blog.common.result.PageResult;
import com.xuyuan.blog.web.dto.moment.MomentPageQuery;
import com.xuyuan.blog.web.vo.moment.MomentVO;

public interface MomentService {

    PageResult<MomentVO> page(MomentPageQuery query);

    MomentVO detail(Long id);
}
