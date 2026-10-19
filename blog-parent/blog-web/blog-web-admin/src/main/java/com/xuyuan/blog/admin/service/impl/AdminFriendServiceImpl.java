package com.xuyuan.blog.admin.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuyuan.blog.admin.dto.*;
import com.xuyuan.blog.admin.mapper.FriendMapper;
import com.xuyuan.blog.admin.service.AdminFriendService;
import com.xuyuan.blog.admin.vo.friend.AdminFriendVO;
import com.xuyuan.blog.common.result.PageResult;
import com.xuyuan.blog.common.result.ResultCode;
import com.xuyuan.blog.model.entity.Friend;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminFriendServiceImpl implements AdminFriendService {
    private final FriendMapper friendMapper;

    @Override
    public PageResult<AdminFriendVO> pageList(FriendQueryParam param) {
        if (param.getPage() < 1) param.setPage(1);
        if (param.getPageSize() < 1 || param.getPageSize() > 100) param.setPageSize(10);
        IPage<AdminFriendVO> result = friendMapper.pageAdminList(new Page<>(param.getPage(), param.getPageSize()), param);
        return PageResult.of(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(FriendCreateParam param) {
        return save(null, param).getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, FriendCreateParam param) {
        save(id, param);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        friendMapper.deleteById(get(id).getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void audit(Long id, FriendAuditParam param) {
        Friend friend = get(id);
        if (param.getStatus() < 1 || param.getStatus() > 2) {
            throw ResultCode.PARAM_ERROR.toException();
        }
        friend.setStatus(param.getStatus());
        friendMapper.updateById(friend);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSort(Long id, SortParam param) {
        Friend friend = get(id);
        friend.setSort(param.getSort());
        friendMapper.updateById(friend);
    }

    private Friend get(Long id) {
        Friend friend = friendMapper.selectById(id);
        if (friend == null) {
            throw ResultCode.ADMIN_FRIEND_NOT_FOUND.toException();
        }
        return friend;
    }

    /** 统一新增与编辑的字段写入逻辑；id 为空时新增，否则更新。 */
    private Friend save(Long id, FriendCreateParam param) {
        Friend friend = id == null ? new Friend() : get(id);
        copy(param, friend);
        if (id == null) {
            friendMapper.insert(friend);
        } else {
            friendMapper.updateById(friend);
        }
        return friend;
    }

    private void copy(FriendCreateParam param, Friend friend) {
        friend.setName(param.getName());
        friend.setUrl(param.getUrl());
        friend.setAvatar(param.getAvatar());
        friend.setDescription(param.getDescription());
        friend.setGroupName(param.getGroup());
        if (param.getStatus() != null) {
            friend.setStatus(param.getStatus());
        } else if (friend.getStatus() == null) {
            friend.setStatus(0);
        }
    }
}
