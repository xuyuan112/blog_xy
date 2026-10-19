package com.xuyuan.blog.web.service.auth;

import com.xuyuan.blog.web.dto.auth.AppChangePasswordParam;
import com.xuyuan.blog.web.dto.auth.AppLoginParam;
import com.xuyuan.blog.web.dto.auth.AppRefreshParam;
import com.xuyuan.blog.web.dto.auth.RegisterParam;
import com.xuyuan.blog.web.vo.auth.AppLoginVO;
import com.xuyuan.blog.web.vo.auth.AppUserInfoVO;

public interface AppAuthService {

    AppLoginVO register(RegisterParam param);

    AppLoginVO login(AppLoginParam param);

    AppLoginVO refresh(AppRefreshParam param);

    AppUserInfoVO userInfo(String accessToken);

    void logout(String accessToken);

    void changePassword(String accessToken, AppChangePasswordParam param);
}
