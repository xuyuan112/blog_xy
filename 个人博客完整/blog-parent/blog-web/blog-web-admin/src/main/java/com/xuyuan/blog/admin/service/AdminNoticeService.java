package com.xuyuan.blog.admin.service;

import com.xuyuan.blog.admin.dto.NoticeCreateParam;
import com.xuyuan.blog.admin.vo.misc.AdminNoticeVO;
import com.xuyuan.blog.common.result.PageResult;

public interface AdminNoticeService {
    PageResult<AdminNoticeVO> pageList(long page, long size);
    Long create(NoticeCreateParam param);
    void update(Long id, NoticeCreateParam param);
    void delete(Long id);
}
