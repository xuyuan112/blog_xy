package com.xuyuan.blog.web.controller.about;

import com.xuyuan.blog.common.result.R;
import com.xuyuan.blog.web.service.about.AboutService;
import com.xuyuan.blog.web.vo.misc.AboutVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "关于我", description = "个人介绍、技能树和履历里程碑")
@RequestMapping("/xuyuan/about")
public class AboutController {

    private final AboutService aboutService;

    @GetMapping
    @Operation(summary = "获取关于我信息", description = "返回个人名片、简介、技能和履历时间轴")
    public R<AboutVO> get() {
        return R.ok(aboutService.get());
    }
}
