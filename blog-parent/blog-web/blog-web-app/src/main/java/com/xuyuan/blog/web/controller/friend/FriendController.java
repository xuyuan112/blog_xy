package com.xuyuan.blog.web.controller.friend;

import com.xuyuan.blog.common.result.R;
import com.xuyuan.blog.web.dto.FriendApplyParam;
import com.xuyuan.blog.web.service.friend.FriendService;
import com.xuyuan.blog.web.vo.friend.FriendListVO;
import com.xuyuan.blog.web.vo.friend.FriendVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "友情链接", description = "审核通过友链的展示和访客申请")
@RequestMapping("/xuyuan/friends")
public class FriendController {

    private final FriendService friendService;

    @GetMapping
    @Operation(summary = "友情链接列表", description = "按分组聚合，仅返回审核通过的友链")
    public R<FriendListVO> list(
            @Parameter(description = "可选分组名称") @RequestParam(required = false) String group
    ) {
        return R.ok(friendService.list(group));
    }

    @GetMapping("/random")
    @Operation(summary = "随机访问友情链接")
    public R<FriendVO> random() {
        return R.ok(friendService.random());
    }

    @PostMapping("/apply")
    @Operation(summary = "提交友情链接申请", description = "申请进入待审核状态，重复站点返回 409")
    public R<Void> apply(@Valid @RequestBody FriendApplyParam param) {
        try {
            friendService.apply(param);
            return R.ok();
        } catch (IllegalArgumentException exception) {
            return R.fail(409, exception.getMessage());
        }
    }
}
