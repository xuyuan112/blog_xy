package com.xuyuan.blog.common.util;

import com.wf.captcha.SpecCaptcha;
import com.wf.captcha.base.Captcha;
import com.xuyuan.blog.common.constant.RedisConstant;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 图形验证码工具
 *
 * <h3>为什么不用 ArithmeticCaptcha</h3>
 * JDK 15+ 移除 Nashorn JavaScript Engine, ArithmeticCaptcha 内部依赖
 * {@code ScriptEngine.eval("1+2*3")} 计算算术表达式结果, 会抛
 * {@code engine is null} NPE。因此改用 {@link SpecCaptcha}(英数 4 位), 完全不碰 ScriptEngine,
 * 与用户提供的示例代码风格一致。
 *
 * <h3>Redis 存储规范 (遵循 RedisConstant 命名体系)</h3>
 * <pre>
 *   Key  : {@link RedisConstant#ADMIN_LOGIN_CAPTCHA_PREFIX} + captchaKey
 *        = blog:admin:login:captcha:{uuid32}
 *   Value: 小写英数文本 (忽略大小写比对)
 *   TTL  : {@link RedisConstant#ADMIN_LOGIN_CAPTCHA_TTL_SEC} 秒 (300s = 5 分钟)
 * </pre>
 * 统一使用 {@link StringRedisTemplate} (UTF-8 字符串序列化), 避免与
 * {@code RedisTemplate<String,Object>} Jackson 序列化格式错位读不到。
 */
@Data
@Component
@RequiredArgsConstructor
public class CaptchaUtil {

    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 生成图形验证码 (英数 4 位, 宽 130 高 48), 写入 Redis (5 分钟 TTL)
     *
     * @return CaptchaResult.captchaKey(无前缀, 直接给前端回传) + captchaImage (Base64)
     */
    public CaptchaResult generate() {
        SpecCaptcha specCaptcha = new SpecCaptcha(130, 48, 4);
        specCaptcha.setCharType(Captcha.TYPE_DEFAULT);

        String code = specCaptcha.text().toLowerCase();
        String captchaKey = UUID.randomUUID().toString().replace("-", "");
        String fullKey = RedisConstant.ADMIN_LOGIN_CAPTCHA_PREFIX + captchaKey;
        String image = specCaptcha.toBase64();

        stringRedisTemplate.opsForValue().set(fullKey, code,
                RedisConstant.ADMIN_LOGIN_CAPTCHA_TTL_SEC, TimeUnit.SECONDS);
        return new CaptchaResult(captchaKey, image);
    }

    /**
     * 仅 true/false 校验 (用于非登录场景), 登录校验走任务书约定的三步分支。
     * 校验通过会立即删除缓存验证码 (一次性消耗, 防暴力重放)。
     */
    public boolean verify(String captchaKey, String captchaCode) {
        if (captchaKey == null || captchaCode == null) {
            return false;
        }
        String fullKey = RedisConstant.ADMIN_LOGIN_CAPTCHA_PREFIX + captchaKey;
        String cached = stringRedisTemplate.opsForValue().get(fullKey);
        if (cached == null) {
            return false;
        }
        boolean ok = cached.equalsIgnoreCase(captchaCode.trim());
        if (ok) {
            stringRedisTemplate.delete(fullKey);
        }
        return ok;
    }

    /**
     * 验证码返回 DTO: captchaKey(给登录接口原样回传) + captchaImage(base64, 可直接 <img src=...>)
     */
    public record CaptchaResult(String captchaKey, String captchaImage) {}
}
