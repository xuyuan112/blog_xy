package com.xuyuan.blog.web.service.friend.impl;

import com.xuyuan.blog.model.entity.Friend;
import com.xuyuan.blog.web.dto.FriendApplyParam;
import com.xuyuan.blog.web.mapper.FriendMapper;
import com.xuyuan.blog.web.service.friend.FriendService;
import com.xuyuan.blog.web.vo.friend.FriendGroupVO;
import com.xuyuan.blog.web.vo.friend.FriendListVO;
import com.xuyuan.blog.web.vo.friend.FriendVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FriendServiceImpl implements FriendService {

    private static final String DEFAULT_GROUP = "其他链接";

    private final FriendMapper friendMapper;

    @Override
    public FriendListVO list(String groupName) {
        List<FriendVO> friends = friendMapper.listApproved(
                StringUtils.hasText(groupName) ? groupName.trim() : null);
        Map<String, List<FriendVO>> grouped = new LinkedHashMap<>();
        for (FriendVO friend : friends) {
            String key = StringUtils.hasText(friend.getGroupName())
                    ? friend.getGroupName() : DEFAULT_GROUP;
            grouped.computeIfAbsent(key, ignored -> new ArrayList<>()).add(friend);
        }

        List<FriendGroupVO> groups = new ArrayList<>();
        grouped.forEach((name, items) -> {
            FriendGroupVO group = new FriendGroupVO();
            group.setName(name);
            group.setFriends(items);
            groups.add(group);
        });

        FriendListVO result = new FriendListVO();
        result.setTotal((long) friends.size());
        result.setGroups(groups);
        return result;
    }

    @Override
    public FriendVO random() {
        return friendMapper.randomApproved();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void apply(FriendApplyParam param) {
        validateUrl(param.getUrl());
        if (friendMapper.countExisting(param.getUrl().trim(), param.getName().trim()) > 0) {
            throw new IllegalArgumentException("该站点已在申请或已存在");
        }

        Friend friend = new Friend();
        friend.setName(param.getName().trim());
        friend.setUrl(param.getUrl().trim());
        friend.setAvatar(trimToNull(param.getAvatar()));
        friend.setDescription(trimToNull(param.getDescription()));
        friend.setEmail(param.getEmail().trim());
        friend.setGroupName(trimToNull(param.getGroup()));
        friend.setStatus(0);
        friend.setSort(0);
        friendMapper.insert(friend);
    }

    private void validateUrl(String value) {
        try {
            URI uri = URI.create(value.trim());
            if (!"http".equalsIgnoreCase(uri.getScheme())
                    && !"https".equalsIgnoreCase(uri.getScheme())
                    || !StringUtils.hasText(uri.getHost())) {
                throw new IllegalArgumentException("站点地址必须是有效的 HTTP/HTTPS URL");
            }
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("站点地址必须是有效的 HTTP/HTTPS URL", exception);
        }
    }

    private String trimToNull(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }
}
