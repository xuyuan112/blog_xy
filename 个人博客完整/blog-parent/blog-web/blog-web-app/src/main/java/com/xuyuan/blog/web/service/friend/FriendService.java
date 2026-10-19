package com.xuyuan.blog.web.service.friend;

import com.xuyuan.blog.web.dto.FriendApplyParam;
import com.xuyuan.blog.web.vo.friend.FriendListVO;
import com.xuyuan.blog.web.vo.friend.FriendVO;

public interface FriendService {

    FriendListVO list(String groupName);

    FriendVO random();

    void apply(FriendApplyParam param);
}
