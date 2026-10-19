package com.xuyuan.blog.admin.custom.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Knife4j / OpenAPI3 接口文档配置(后台管理端)
 *
 * <p>基于 OpenAPI 3 规范定义后台管理端接口文档, 包含:
 * <ul>
 *   <li>文档基础信息(标题、描述、版本、联系方式、许可证)</li>
 *   <li>JWT Bearer 认证方案(Knife4j 界面"Authorize"按钮)</li>
 *   <li>按业务领域分组(认证/文章/评论/友链/相册/说说/仪表盘/文件)</li>
 * </ul>
 *
 * <p>访问地址:
 * <ul>
 *   <li>Knife4j 增强文档: <a href="http://localhost:8080/doc.html">http://localhost:8080/doc.html</a></li>
 *   <li>原生 Swagger UI: <a href="http://localhost:8080/swagger-ui.html">http://localhost:8080/swagger-ui.html</a></li>
 *   <li>OpenAPI JSON: <a href="http://localhost:8080/v3/api-docs">http://localhost:8080/v3/api-docs</a></li>
 * </ul>
 */
@Configuration
public class Knife4jConfiguration {

    /**
     * JWT 认证方案名称, 与 SecurityRequirement 引用一致。
     * 为简化 Knife4j 使用, 不再要求 "Authorization: Bearer xxx" 前缀, 改为直接上送 Header:
     *   access-token: &lt;login 返回的 accessToken 纯值&gt;
     */
    public static final String SECURITY_SCHEME_NAME = "access-token";

    /**
     * OpenAPI 基础配置
     *
     * <p>定义文档元信息与全局 JWT 安全方案, 所有接口默认应用认证。</p>
     *
     * @return OpenAPI 配置实例
     */
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("个人全栈博客系统 API - 后台管理端")
                        .version("1.0.0")
                        .description("前后台分离的博客系统接口文档(后台管理端)\n\n"
                                + "提供文章、分类、标签、评论、友链、相册、说说、用户等管理接口。\n"
                                + "除登录/验证码等公开接口外, 所有接口在「调试」卡片顶部的 Header 区填入 access-token, 值就是登录接口返回的 accessToken 纯 JWT 字符串(无需 Bearer 前缀)。")
                        .contact(new Contact()
                                .name("xuyuan")
                                .url("https://github.com/xuyuan")
                                .email("xuyuan@example.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME, new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .name("access-token")
                                .description("JWT 访问令牌(APIKEY 模式, 无需 Bearer 前缀)\n"
                                        + "登录成功后由 /xuyuan/auth/login 返回的 accessToken 纯字符串直接粘贴即可,\n"
                                        + "Knife4j 会自动在每个请求 Header 中增加: access-token: <粘贴的 JWT>")));
    }

    /**
     * 认证管理分组
     *
     * <p>登录、登出、验证码、当前用户信息等接口(API_SPEC 第 2 节)。</p>
     */
    @Bean
    public GroupedOpenApi authAPI() {
        return GroupedOpenApi.builder()
                .group("认证管理")
                .pathsToMatch("/xuyuan/auth/**")
                .build();
    }

    /**
     * 文章管理分组
     *
     * <p>文章 CRUD、状态变更、草稿保存; 分类/标签 CRUD(API_SPEC 第 13、14 节)。</p>
     */
    @Bean
    public GroupedOpenApi articleAPI() {
        return GroupedOpenApi.builder()
                .group("文章管理")
                .pathsToMatch("/xuyuan/admin/articles/**", "/xuyuan/admin/categories/**", "/xuyuan/admin/tags/**")
                .build();
    }

    /**
     * 评论管理分组
     *
     * <p>评论审核/隐藏/删除(API_SPEC 第 18 节)。</p>
     */
    @Bean
    public GroupedOpenApi commentAPI() {
        return GroupedOpenApi.builder()
                .group("评论管理")
                .pathsToMatch("/xuyuan/admin/comments/**")
                .build();
    }

    /**
     * 友链管理分组
     *
     * <p>友链 CRUD、审核、排序(API_SPEC 第 17 节)。</p>
     */
    @Bean
    public GroupedOpenApi friendAPI() {
        return GroupedOpenApi.builder()
                .group("友链管理")
                .pathsToMatch("/xuyuan/admin/friends/**")
                .build();
    }

    /**
     * 相册管理分组
     *
     * <p>相册 CRUD、照片批量上传/删除(API_SPEC 第 16 节)。</p>
     */
    @Bean
    public GroupedOpenApi galleryAPI() {
        return GroupedOpenApi.builder()
                .group("相册管理")
                .pathsToMatch("/xuyuan/admin/galleries/**", "/xuyuan/admin/photos/**")
                .build();
    }

    /**
     * 说说管理分组
     *
     * <p>说说 CRUD(API_SPEC 第 15 节)。</p>
     */
    @Bean
    public GroupedOpenApi momentAPI() {
        return GroupedOpenApi.builder()
                .group("说说管理")
                .pathsToMatch("/xuyuan/admin/moments/**")
                .build();
    }

    /**
     * 仪表盘统计分组
     *
     * <p>总览统计、访问趋势(API_SPEC 第 20.1/20.2 节)。</p>
     */
    @Bean
    public GroupedOpenApi dashboardAPI() {
        return GroupedOpenApi.builder()
                .group("仪表盘统计")
                .pathsToMatch("/xuyuan/admin/dashboard/**")
                .build();
    }

    /**
     * 文件管理分组
     *
     * <p>通用文件上传、图片专用上传(API_SPEC 第 19 节)。</p>
     */
    @Bean
    public GroupedOpenApi fileAPI() {
        return GroupedOpenApi.builder()
                .group("文件管理")
                .pathsToMatch("/xuyuan/admin/files/**")
                .build();
    }

    /**
     * 公告一言管理分组
     *
     * <p>公告 CRUD、一言 CRUD(API_SPEC 第 21 节)。</p>
     */
    @Bean
    public GroupedOpenApi noticeWordAPI() {
        return GroupedOpenApi.builder()
                .group("公告一言管理")
                .pathsToMatch("/xuyuan/admin/notices/**", "/xuyuan/admin/words/**")
                .build();
    }

    /**
     * 关于我日程管理分组
     *
     * <p>关于我配置、日程 CRUD(API_SPEC 第 22 节)。</p>
     */
    @Bean
    public GroupedOpenApi aboutScheduleAPI() {
        return GroupedOpenApi.builder()
                .group("关于我日程管理")
                .pathsToMatch("/xuyuan/admin/about", "/xuyuan/admin/schedules/**")
                .build();
    }

    /**
     * 站点配置分组
     *
     * <p>站点配置查询/更新(API_SPEC 第 20.3 节)。</p>
     */
    @Bean
    public GroupedOpenApi configAPI() {
        return GroupedOpenApi.builder()
                .group("站点配置")
                .pathsToMatch("/xuyuan/admin/configs")
                .build();
    }

    /**
     * 前台用户管理分组
     *
     * <p>后台管理前台注册用户: 分页查询/详情/禁用启用/重置密码/删除
     * (API_SPEC 第 24 节, 对应任务书 M13)。</p>
     */
    @Bean
    public GroupedOpenApi userAPI() {
        return GroupedOpenApi.builder()
                .group("前台用户管理")
                .pathsToMatch("/xuyuan/admin/users/**")
                .build();
    }
}
