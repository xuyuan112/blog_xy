package com.xuyuan.blog.common.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.xuyuan.blog.common.context.CurrentUserContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * MyBatis-Plus 字段自动填充
 *
 * <p>在插入/更新时自动填充 {@code BaseEntity} 中的 createTime/updateTime/createBy/updateBy。
 * 当前用户 ID 直接从 {@link CurrentUserContext#getUserId()} 读取
 * (由 JwtAuthenticationFilter 在解析 JWT 后一次性写入 ThreadLocal, 避免重复解析 JWT)。
 */
@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        LocalDateTime now = LocalDateTime.now();
        Long userId = currentUserId();
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, now);
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, now);
        this.strictInsertFill(metaObject, "createBy", Long.class, userId);
        this.strictInsertFill(metaObject, "updateBy", Long.class, userId);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        Long userId = currentUserId();
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
        this.strictUpdateFill(metaObject, "updateBy", Long.class, userId);
    }

    /**
     * 当前登录用户 ID, 未登录或启动脚本执行时返回 0。
     * 由 JwtAuthenticationFilter 在 parseToken 成功后, 一次性写入 ThreadLocal。
     *
     * <p>⚠️ 方法名加 Current 前缀: 静态方法 {@code CurrentUserContext.getCurrentUserId()}
     * 刻意与 Lombok @Data 实例 getter {@code ctx.getUserId()} 区分, 避免 Lombok 跳过生成 getter
     * 导致 ctx.getUserId() 误调用静态方法造成无限递归 StackOverflowError。
     */
    private Long currentUserId() {
        Long uid = CurrentUserContext.getCurrentUserId();
        return uid == null ? 0L : uid;
    }
}
