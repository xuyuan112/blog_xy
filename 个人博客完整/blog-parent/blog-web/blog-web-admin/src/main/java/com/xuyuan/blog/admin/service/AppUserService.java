package com.xuyuan.blog.admin.service;

import com.xuyuan.blog.admin.dto.AppUserPageParam;
import com.xuyuan.blog.admin.dto.AppUserResetPasswordParam;
import com.xuyuan.blog.admin.dto.AppUserStatusParam;
import com.xuyuan.blog.admin.vo.user.AppUserAdminVO;
import com.xuyuan.blog.common.result.PageResult;

public interface AppUserService {
    PageResult<AppUserAdminVO> pageList(AppUserPageParam param);
    AppUserAdminVO detail(Long id);
    void changeStatus(Long id, AppUserStatusParam param);
    void resetPassword(Long id, AppUserResetPasswordParam param);
    void delete(Long id);
}
