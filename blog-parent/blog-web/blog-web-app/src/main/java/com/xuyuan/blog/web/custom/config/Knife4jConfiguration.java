package com.xuyuan.blog.web.custom.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Knife4j/OpenAPI 3 配置：展示端接口按业务域分组，便于联调和前端查阅。 */
@Configuration
public class Knife4jConfiguration {
    public static final String SECURITY_SCHEME_NAME = "bearer-token";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("个人全栈博客系统 API - 前台展示端")
                        .version("1.2.0")
                        .description("博客用户展示端 REST API。所有响应使用 {code, message, data} 结构；\n"
                                + "公开浏览接口无需登录，评论、点赞及用户认证接口按实际鉴权策略执行。\n"
                                + "调试地址：/doc.html；OpenAPI JSON：/v3/api-docs。")
                        .contact(new Contact().name("xuyuan").url("https://github.com/xuyuan").email("xuyuan@example.com"))
                        .license(new License().name("Apache 2.0").url("https://www.apache.org/licenses/LICENSE-2.0")))
                .components(new Components().addSecuritySchemes(SECURITY_SCHEME_NAME,
                        new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer")
                                .bearerFormat("JWT").in(SecurityScheme.In.HEADER).name("Authorization")
                                .description("需要登录时填写 Bearer <accessToken>。accessToken 来自 /xuyuan/app/auth/login。")));
    }

    @Bean
    public GroupedOpenApi siteAPI() {
        return group("首页与站点服务", "/xuyuan/site/**");
    }

    @Bean
    public GroupedOpenApi articleAPI() {
        return group("文章与归档", "/xuyuan/articles/**", "/xuyuan/categories/**", "/xuyuan/tags/**", "/xuyuan/archives/**");
    }

    @Bean
    public GroupedOpenApi momentAPI() {
        return group("说说与杂谈", "/xuyuan/moments/**", "/xuyuan/thoughts/**");
    }

    @Bean
    public GroupedOpenApi galleryAPI() {
        return group("照片墙", "/xuyuan/galleries/**", "/xuyuan/photos/**");
    }

    @Bean
    public GroupedOpenApi interactionAPI() {
        return group("评论与点赞", "/xuyuan/comments/**", "/xuyuan/likes/**");
    }

    @Bean
    public GroupedOpenApi friendAPI() {
        return group("友情链接", "/xuyuan/friends/**");
    }

    @Bean
    public GroupedOpenApi aboutAPI() {
        return group("关于我", "/xuyuan/about/**");
    }

    @Bean
    public GroupedOpenApi appAuthAPI() {
        return group("前台用户认证", "/xuyuan/app/auth/**");
    }

    private GroupedOpenApi group(String name, String... paths) {
        return GroupedOpenApi.builder().group(name).pathsToMatch(paths).build();
    }
}
