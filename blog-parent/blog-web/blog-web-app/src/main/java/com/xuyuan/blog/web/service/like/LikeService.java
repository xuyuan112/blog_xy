package com.xuyuan.blog.web.service.like;

import com.xuyuan.blog.web.dto.LikeToggleParam;
import com.xuyuan.blog.web.vo.comment.LikeToggleVO;

public interface LikeService {

    LikeToggleVO toggle(LikeToggleParam param, String clientIp);
}
