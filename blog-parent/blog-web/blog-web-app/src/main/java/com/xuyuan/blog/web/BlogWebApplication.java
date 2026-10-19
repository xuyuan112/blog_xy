package com.xuyuan.blog.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.mybatis.spring.annotation.MapperScan;

/**
 * 前台展示端启动类
 *
 * <p>服务端口默认 8080, 承载 /xuyuan/** 前台展示接口。</p>
 */
@SpringBootApplication(scanBasePackages = {
        "com.xuyuan.blog.web",
        "com.xuyuan.blog.common",
        "com.xuyuan.blog.model"
})
@MapperScan("com.xuyuan.blog.web.mapper")
public class BlogWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(BlogWebApplication.class, args);
    }
}
