package com.xuyuan.blog.common.context;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 当前登录用户上下文(跨模块通用结构)
 *
 * <h3>设计: 拦截器一次解析, 全局复用 (避免 JWT 重复解析)</h3>
 * <p>作为 "SecurityContext ↔ MyMetaObjectHandler ↔ 业务层" 之间的 DTO,
 * 放在 blog-common 层以避免 blog-common 反向依赖 blog-web-admin。
 *
 * <h3>ThreadLocal 存取规范(严格遵守否则会串号!)</h3>
 * <ol>
 *   <li>{@code set(ctx)}: 由 JwtAuthenticationFilter 在解析 JWT 成功后立即调用</li>
 *   <li>{@code get() / getCurrentUserId() / getCurrentUsername()}: 任何位置均可无侵入调用, 未登录返回 {@code null}</li>
 *   <li>{@code clear()}: <b>必须由 JwtAuthenticationFilter 在 finally 中调用</b>
 *       (Tomcat 线程池复用, 若不 clear, 会把上一个用户的上下文串到下一个请求上)</li>
 * </ol>
 *
 * <p>⚠️ <b>静态方法命名刻意避开实例 getter: 避免 Lombok @Data 检测到同名方法后跳过生成 getter,
 * 导致 {@code ctx.getUserId()} 误调用静态方法引发无限递归 → StackOverflowError。</b></p>
 *
 * <p>使用 InheritableThreadLocal 而非 ThreadLocal: 当业务代码内起新线程(如 @Async)时,
 * 子线程可以继承父线程的当前用户上下文, 避免异步任务丢失 "updateBy 自动填充"。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CurrentUserContext {

    /** 用户 ID (t_user.id) */
    private Long userId;

    /** 用户名 (t_user.username) */
    private String username;

    // ======================================================================
    //                    ThreadLocal 容器 + 静态便捷方法
    // ======================================================================

    private static final ThreadLocal<CurrentUserContext> CONTEXT = new InheritableThreadLocal<>();

    /** 写入当前请求上下文 (由 JwtAuthenticationFilter 成功分支调用) */
    public static void set(CurrentUserContext ctx) {
        CONTEXT.set(ctx);
    }

    /** 获取当前请求上下文, 未登录返回 null */
    public static CurrentUserContext get() {
        return CONTEXT.get();
    }

    /**
     * 便捷方法: 直接拿当前登录 userId, 未登录或未解析返回 null。
     * <p><b>刻意加 Current 前缀</b>: 避开 Lombok @Data 为字段 userId 自动生成的同名实例 getter,
     * 防止无限递归 StackOverflowError (参见类 javadoc 顶部警告)。
     */
    public static Long getCurrentUserId() {
        CurrentUserContext ctx = CONTEXT.get();
        return ctx == null ? null : ctx.getUserId();
    }

    /**
     * 便捷方法: 直接拿当前登录 username, 未登录或未解析返回 null。
     * <p><b>刻意加 Current 前缀</b>: 避开 Lombok @Data 自动生成的实例 getter, 防止无限递归。
     */
    public static String getCurrentUsername() {
        CurrentUserContext ctx = CONTEXT.get();
        return ctx == null ? null : ctx.getUsername();
    }

    /**
     * 清理当前请求上下文 (线程归还给线程池前必须调用, 防止身份串号)。
     * 即使 set(null) 也要 clear, 避免 InheritableThreadLocal 内存泄漏。
     */
    public static void clear() {
        CONTEXT.remove();
    }

    // ======================================================================
    //                           静态工厂
    // ======================================================================

    public static CurrentUserContext of(Long userId, String username) {
        return new CurrentUserContext(userId, username);
    }
}
