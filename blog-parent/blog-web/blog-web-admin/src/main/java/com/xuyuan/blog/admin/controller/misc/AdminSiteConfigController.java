package com.xuyuan.blog.admin.controller.misc;

import com.xuyuan.blog.admin.custom.config.Knife4jConfiguration;
import com.xuyuan.blog.admin.service.AdminSiteConfigService;
import com.xuyuan.blog.common.result.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Tag(name = "后台站点配置")
@RestController
@RequestMapping("/xuyuan/admin/configs")
@RequiredArgsConstructor
@SecurityRequirement(name = Knife4jConfiguration.SECURITY_SCHEME_NAME)
public class AdminSiteConfigController {

    private final AdminSiteConfigService service;

    @GetMapping
    @Operation(summary = "查询站点配置")
    public R<Map<String, String>> getAll() {
        return R.ok(service.getAll());
    }

    @PutMapping
    @Operation(summary = "更新站点配置")
    public R<Void> update(@RequestBody Map<String, String> configs) {
        service.update(configs);
        return R.ok();
    }
}
