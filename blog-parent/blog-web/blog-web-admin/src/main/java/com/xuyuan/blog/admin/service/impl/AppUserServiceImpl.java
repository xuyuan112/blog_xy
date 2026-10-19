package com.xuyuan.blog.admin.service.impl;

import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuyuan.blog.admin.dto.AppUserPageParam;
import com.xuyuan.blog.admin.dto.AppUserResetPasswordParam;
import com.xuyuan.blog.admin.dto.AppUserStatusParam;
import com.xuyuan.blog.admin.mapper.AppUserMapper;
import com.xuyuan.blog.admin.service.AppUserService;
import com.xuyuan.blog.admin.vo.user.AppUserAdminVO;
import com.xuyuan.blog.common.result.PageResult;
import com.xuyuan.blog.common.result.ResultCode;
import com.xuyuan.blog.model.entity.AppUser;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AppUserServiceImpl implements AppUserService {
    private final AppUserMapper appUserMapper;

    @Override
    public PageResult<AppUserAdminVO> pageList(AppUserPageParam param) {
        long current = param.getCurrent() == null || param.getCurrent() < 1 ? 1 : param.getCurrent();
        long size = param.getSize() == null || param.getSize() < 1 || param.getSize() > 100 ? 10 : param.getSize();
        LambdaQueryWrapper<AppUser> wrapper = new LambdaQueryWrapper<AppUser>()
                .like(param.getUsername() != null && !param.getUsername().isBlank(), AppUser::getUsername, param.getUsername())
                .like(param.getNickname() != null && !param.getNickname().isBlank(), AppUser::getNickname, param.getNickname())
                .eq(param.getStatus() != null, AppUser::getStatus, param.getStatus())
                .orderByDesc(AppUser::getCreateTime);
        IPage<AppUser> page = appUserMapper.selectPage(new Page<>(current, size), wrapper);
        return PageResult.of(page.getRecords().stream().map(this::toVO).toList(), page.getTotal(), page.getCurrent(), page.getSize());
    }

    @Override
    public AppUserAdminVO detail(Long id) {
        return toVO(get(id));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeStatus(Long id, AppUserStatusParam param) {
        if (param.getStatus() != 0 && param.getStatus() != 1) throw ResultCode.PARAM_ERROR.toException();
        AppUser user = get(id);
        user.setStatus(param.getStatus());
        appUserMapper.updateById(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetPassword(Long id, AppUserResetPasswordParam param) {
        AppUser user = get(id);
        user.setPassword(SecureUtil.md5(param.getNewPassword()));
        appUserMapper.updateById(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        appUserMapper.deleteById(get(id).getId());
    }

    private AppUser get(Long id) {
        AppUser user = appUserMapper.selectById(id);
        if (user == null) throw ResultCode.APP_USER_NOT_FOUND.toException();
        return user;
    }

    private AppUserAdminVO toVO(AppUser user) {
        AppUserAdminVO vo = new AppUserAdminVO();
        BeanUtils.copyProperties(user, vo);
        return vo;
    }
}
