package com.xuyuan.blog.common.result;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.xuyuan.blog.common.exception.BusinessException;

/**
 * 统一返回结果状态信息类
 *
 * <p>采用三位数业务码风格, 按段划分:</p>
 * <ul>
 *   <li><b>2xx</b> 通用段: 系统级通用状态</li>
 *   <li><b>3xx</b> 后台管理端段: blog-web-admin 业务专用</li>
 *   <li><b>5xx</b> 前台展示端段: blog-web-app 业务专用</li>
 *   <li><b>6xx</b> Token 段: 认证 token 相关</li>
 * </ul>
 *
 * <p><b>HTTP 响应行与业务码的关系</b>: HTTP 行统一返回 200,
 * 业务结果由 {@code R.code} 字段表达。前端按 {@code code} 精确判断业务类型,
 * 按 {@code message} 进行用户提示。</p>
 *
 * <pre>
 * 用法:
 *   // 抛业务异常(用默认 message)
 *   throw ResultCode.ADMIN_CAPTCHA_CODE_ERROR.toException();
 *
 *   // 抛业务异常(自定义 message, 覆盖默认)
 *   throw ResultCode.ADMIN_ACCOUNT_ERROR.toException("用户名或密码错误");
 *
 *   // 返回失败响应
 *   return ResultCode.PARAM_ERROR.toR();
 * </pre>
 */
public enum ResultCode implements BaseEnum<Integer> {

    // --------------------------------------------------------------------------
    // 2xx 通用段
    // --------------------------------------------------------------------------
    SUCCESS(200, "成功"),
    FAIL(201, "失败"),
    PARAM_ERROR(202, "参数不正确"),
    SERVICE_ERROR(203, "服务异常"),
    DATA_ERROR(204, "数据异常"),
    ILLEGAL_REQUEST(205, "非法请求"),
    REPEAT_SUBMIT(206, "重复提交"),
    DELETE_ERROR(207, "请先删除子集"),

    // --------------------------------------------------------------------------
    // 3xx 后台管理端段 (blog-web-admin)
    // --------------------------------------------------------------------------
    /** 账号已存在(注册/新增管理员时) */
    ADMIN_ACCOUNT_EXIST_ERROR(301, "账号已存在"),
    /** 验证码错误 */
    ADMIN_CAPTCHA_CODE_ERROR(302, "验证码错误"),
    /** 验证码已过期 */
    ADMIN_CAPTCHA_CODE_EXPIRED(303, "验证码已过期"),
    /** 未输入验证码 */
    ADMIN_CAPTCHA_CODE_NOT_FOUND(304, "未输入验证码"),
    /** 未登录 */
    ADMIN_LOGIN_AUTH(305, "未登录"),
    /** 账号不存在(查询指定用户时, 登录场景防枚举不用此码) */
    ADMIN_ACCOUNT_NOT_EXIST_ERROR(306, "账号不存在"),
    /** 用户名或密码错误(登录场景统一用此码, 防账号枚举) */
    ADMIN_ACCOUNT_ERROR(307, "用户名或密码错误"),
    /** 该用户已被禁用 */
    ADMIN_ACCOUNT_DISABLED_ERROR(308, "该用户已被禁用"),
    /** 无访问权限 */
    ADMIN_ACCESS_FORBIDDEN(309, "无访问权限"),
    /** 文章不存在 */
    ADMIN_ARTICLE_NOT_FOUND(310, "文章不存在"),
    /** 分类不存在 */
    ADMIN_CATEGORY_NOT_FOUND(311, "分类不存在"),
    /** 标签不存在 */
    ADMIN_TAG_NOT_FOUND(312, "标签不存在"),
    /** 分类下有文章, 无法删除 */
    ADMIN_CATEGORY_HAS_ARTICLES(313, "请先删除该分类下的文章"),
    /** 评论不存在 */
    ADMIN_COMMENT_NOT_FOUND(314, "评论不存在"),
    /** 说说不存在 */
    ADMIN_MOMENT_NOT_FOUND(315, "说说不存在"),
    /** 相册不存在 */
    ADMIN_GALLERY_NOT_FOUND(316, "相册不存在"),
    /** 友链不存在 */
    ADMIN_FRIEND_NOT_FOUND(317, "友链不存在"),
    /** 文章状态不允许该操作 */
    ADMIN_ARTICLE_STATUS_ERROR(318, "文章状态不允许该操作"),
    /** 文件上传失败 */
    ADMIN_FILE_UPLOAD_ERROR(319, "文件上传失败"),
    /** 不支持的文件类型 */
    ADMIN_FILE_TYPE_ERROR(320, "不支持的文件类型"),
    /** 文件大小超限 */
    ADMIN_FILE_SIZE_ERROR(321, "文件大小超限"),

    // --------------------------------------------------------------------------
    // 5xx 前台展示端段 (blog-web-app)
    // --------------------------------------------------------------------------
    /** 未登录(前台游客访问需登录接口) */
    APP_LOGIN_AUTH(501, "未登录"),
    /** 该用户已被禁用 */
    APP_ACCOUNT_DISABLED_ERROR(502, "该用户已被禁用"),
    /** 评论内容为空 */
    APP_COMMENT_CONTENT_EMPTY(503, "评论内容为空"),
    /** 评论过于频繁 */
    APP_COMMENT_TOO_OFTEN(504, "评论过于频繁"),
    /** 点赞过于频繁 */
    APP_LIKE_TOO_OFTEN(505, "点赞过于频繁"),
    /** 友链申请过于频繁 */
    APP_FRIEND_APPLY_TOO_OFTEN(506, "友链申请过于频繁"),
    /** 前台账号已存在(注册场景) */
    APP_ACCOUNT_EXIST_ERROR(507, "账号已存在"),
    /** 前台用户名或密码错误(登录场景, 防账号枚举) */
    APP_ACCOUNT_ERROR(508, "用户名或密码错误"),
    /** 前台用户不存在(后台管理场景, 查询指定用户时) */
    APP_USER_NOT_FOUND(509, "用户不存在"),
    /** 前台旧密码错误(修改密码场景) */
    APP_OLD_PASSWORD_ERROR(510, "原密码错误"),
    /** 前台用户名不能为空 */
    APP_USERNAME_EMPTY(511, "用户名不能为空"),
    /** 前台密码不能为空 */
    APP_PASSWORD_EMPTY(512, "密码不能为空"),
    /** 前台昵称不能为空 */
    APP_NICKNAME_EMPTY(513, "昵称不能为空"),

    // --------------------------------------------------------------------------
    // 6xx Token 段
    // --------------------------------------------------------------------------
    /** token 过期 */
    TOKEN_EXPIRED(601, "token过期"),
    /** token 非法 */
    TOKEN_INVALID(602, "token非法"),
    /** token 缺失(请求头未携带 token) */
    TOKEN_MISSING(603, "token缺失"),
    ;

    @EnumValue
    private final Integer code;
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public Integer getCode() {
        return code;
    }

    /**
     * 返回状态描述信息
     *
     * <p>注意: 本枚举的提示文案字段名为 {@code message},
     * 对应 {@link BaseEnum#getLabel()} 接口方法。</p>
     */
    @Override
    public String getLabel() {
        return message;
    }

    /** 获取 message(语义更直观的别名) */
    public String getMessage() {
        return message;
    }

    /**
     * 转换为业务异常, 使用默认 message
     */
    public BusinessException toException() {
        return new BusinessException(code, message);
    }

    /**
     * 转换为业务异常, 使用自定义 message(覆盖默认)
     *
     * <p>用于同一枚举内需要动态 message 的场景, 例如:</p>
     * <pre>
     * throw ResultCode.PARAM_ERROR.toException("标题不能为空");
     * </pre>
     */
    public BusinessException toException(String message) {
        return new BusinessException(code, message);
    }

    /**
     * 转换为失败响应, 使用默认 message
     */
    public <T> R<T> toR() {
        return R.fail(code, message);
    }

    /**
     * 转换为失败响应, 使用自定义 message
     */
    public <T> R<T> toR(String message) {
        return R.fail(code, message);
    }
}
