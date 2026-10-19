package com.xuyuan.blog.web.controller.like;

import com.xuyuan.blog.common.result.R;
import com.xuyuan.blog.web.dto.LikeToggleParam;
import com.xuyuan.blog.web.service.like.LikeService;
import com.xuyuan.blog.web.vo.comment.LikeToggleVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "点赞互动", description = "文章和说说的按客户端 IP 点赞切换")
@RequestMapping("/xuyuan/likes")
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/toggle")
    @Operation(summary = "点赞或取消点赞", description = "同一客户端重复调用会在点赞和取消之间切换")
    public R<LikeToggleVO> toggle(
            @Valid @RequestBody LikeToggleParam param,
            HttpServletRequest request
    ) {
        try {
            return R.ok(likeService.toggle(param, resolveClientIp(request)));
        } catch (IllegalArgumentException exception) {
            return R.fail(400, exception.getMessage());
        }
    }

    private String resolveClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
