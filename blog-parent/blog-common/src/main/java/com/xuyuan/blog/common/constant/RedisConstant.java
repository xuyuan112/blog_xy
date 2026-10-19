package com.xuyuan.blog.common.constant;

/**
 * 项目 Redis Key 与 TTL 常量定义
 *
 * <h3>命名规范 (用户约定)</h3>
 * <pre>
 *   项目名 : 功能模块名 : 其他
 *   ─────   ──────────   ─────
 *   blog    admin/login   captcha:abc123...
 *
 *   前缀层级:
 *     blog:            - 个人博客项目根前缀 (所有 key 都带, 避免多项目共用 Redis 时冲突)
 *     blog:admin:*     - 后台管理端模块
 *     blog:app:*       - 前台展示端模块 (M3 以后再使用)
 * </pre>
 *
 * <h3>TTL 常量命名</h3>
 * 后缀带单位 {@code _SEC / _MIN / _HOUR / _DAY}, 避免魔法值歧义。
 */
public final class RedisConstant {

    private RedisConstant() {
        // 工具类, 不允许实例化
    }

    // ========================================================================
    //  后台管理端 - 登录认证模块 (blog:admin:login:*)
    // ========================================================================

    /** 后台登录: 图形验证码 key 前缀 (最后追加 32 位 captchaKey, value = 小写英数文本) */
    public static final String ADMIN_LOGIN_CAPTCHA_PREFIX = "blog:admin:login:captcha:";
    /** 后台登录: 图形验证码 TTL (5 分钟 = 300 秒, 与任务书 M2 一致) */
    public static final long ADMIN_LOGIN_CAPTCHA_TTL_SEC = 5L * 60;

    /**
     * 后台登录: JWT Token 黑名单集合 (Set 结构, 存储已登出/同账号互踢后失效的 accessToken/refreshToken)
     * 集合整体 TTL 近似取 refresh 有效期 7 天, 避免永远堆积。
     */
    public static final String ADMIN_LOGIN_TOKEN_BLACKLIST = "blog:admin:login:token:blacklist";
    /** 后台登录: Token 黑名单集合整体过期时间 (兜底, 7 天 = 604800 秒) */
    public static final long ADMIN_LOGIN_TOKEN_BLACKLIST_TTL_SEC = 7L * 24 * 60 * 60;

    /**
     * 后台登录: 单用户当前 refreshToken 登记集合 (key = blog:admin:login:token:user-refresh:{userId})
     * 登录 / refresh 成功时登记新的 refreshToken, 下次新登录时把集合内旧 token 全部加入黑名单, 实现"同账号互踢"。
     */
    public static final String ADMIN_LOGIN_USER_REFRESH_PREFIX = "blog:admin:login:token:user-refresh:";

    // ========================================================================
    //  前台展示端 - 登录认证模块 (blog:app:login:*)
    //  预留, 开发 M3~M 系列前台端时再使用, 不抄示例代码。
    // ========================================================================

    /** 前台用户登录缓存前缀 (预留) */
    public static final String APP_LOGIN_PREFIX = "blog:app:login:";
    /** 前台 6 位邮箱验证码重发冷却时间 (秒, 预留) */
    public static final long APP_LOGIN_CODE_RESEND_TIME_SEC = 60L;
    /** 前台邮箱验证码 TTL (10 分钟 = 600 秒, 预留) */
    public static final long APP_LOGIN_CODE_TTL_SEC = 10L * 60;

    /** 前台 Token 黑名单（登出后的 access/refresh token）。 */
    public static final String APP_LOGIN_TOKEN_BLACKLIST = "blog:app:login:token:blacklist";

    /** 点赞短时防刷锁前缀，避免同一客户端并发重复提交。 */
    public static final String APP_LIKE_LOCK_PREFIX = "blog:app:like:lock:";

    /** 点赞短锁有效期，过期后仍可正常执行下一次切换。 */
    public static final long APP_LIKE_LOCK_TTL_SEC = 2L;
}
