package com.xuyuan.blog.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

/**
 * 后台管理端启动类
 *
 * <p>服务端口默认 8080, 承载 /xuyuan/admin/** 管理接口。
 * 排除 Spring Security 的 UserDetailsServiceAutoConfiguration:
 * 后台登录不通过 DaoAuthenticationProvider 而是在 AuthServiceImpl 中手动 MD5 比对+签发 JWT,
 * 这样启动日志不会打印 "Using generated security password"。
 */
@SpringBootApplication(
        scanBasePackages = "com.xuyuan.blog",
        exclude = UserDetailsServiceAutoConfiguration.class
)
@MapperScan({
        "com.xuyuan.blog.admin.mapper",
        "com.xuyuan.blog.web.mapper"
})
public class BlogAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(BlogAdminApplication.class, args);
    }
}
