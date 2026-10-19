package com.xuyuan.blog.admin.service;

import com.xuyuan.blog.admin.dto.*;
import com.xuyuan.blog.admin.vo.friend.AdminFriendVO;
import com.xuyuan.blog.common.result.PageResult;

public interface AdminFriendService {
    PageResult<AdminFriendVO> pageList(FriendQueryParam param);
    Long create(FriendCreateParam param);
    void update(Long id, FriendCreateParam param);
    void delete(Long id);
    void audit(Long id, FriendAuditParam param);
    void updateSort(Long id, SortParam param);
}
