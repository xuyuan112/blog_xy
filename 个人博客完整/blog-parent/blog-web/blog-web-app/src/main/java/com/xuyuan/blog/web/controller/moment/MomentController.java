package com.xuyuan.blog.web.controller.moment;

import com.xuyuan.blog.common.result.PageResult;
import com.xuyuan.blog.common.result.R;
import com.xuyuan.blog.web.dto.moment.MomentPageQuery;
import com.xuyuan.blog.web.service.moment.MomentService;
import com.xuyuan.blog.web.vo.moment.MomentVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "说说展示", description = "前台公开说说的时间轴和详情")
@RequestMapping("/xuyuan/moments")
public class MomentController {

    private final MomentService momentService;

    @GetMapping
    @Operation(summary = "说说分页列表", description = "按发布时间倒序返回公开说说")
    public R<PageResult<MomentVO>> page(@ParameterObject MomentPageQuery query) {
        return R.ok(momentService.page(query));
    }

    @GetMapping("/{id}")
    @Operation(summary = "说说详情")
    public R<MomentVO> detail(
            @Parameter(description = "说说 ID") @PathVariable Long id
    ) {
        MomentVO result = momentService.detail(id);
        return result == null ? R.fail(404, "说说不存在或不可见") : R.ok(result);
    }
}
