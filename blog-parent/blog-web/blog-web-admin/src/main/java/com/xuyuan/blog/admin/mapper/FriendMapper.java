package com.xuyuan.blog.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuyuan.blog.admin.dto.FriendQueryParam;
import com.xuyuan.blog.admin.vo.friend.AdminFriendVO;
import com.xuyuan.blog.model.entity.Friend;
import org.apache.ibatis.annotations.Param;

public interface FriendMapper extends BaseMapper<Friend> {
    IPage<AdminFriendVO> pageAdminList(Page<AdminFriendVO> page, @Param("param") FriendQueryParam param);
}
