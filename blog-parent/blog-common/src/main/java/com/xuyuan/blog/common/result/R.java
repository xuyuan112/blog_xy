package com.xuyuan.blog.common.result;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

/**
 * 统一响应结果
 *
 * <p>code 采用标准 HTTP 状态码语义, 200 表示成功。</p>
 *
 * @param <T> 业务数据类型
 */
@Data
public class R<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 标准 HTTP 状态码, 200 表示成功 */
    private Integer code;

    /** 提示信息 */
    private String message;

    /** 业务数据 */
    private T data;

    public static <T> R<T> ok() {
        return build(HttpStatus.OK.value(), "success", null);
    }

    public static <T> R<T> ok(T data) {
        return build(HttpStatus.OK.value(), "success", data);
    }

    public static <T> R<T> fail(String message) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR.value(), message, null);
    }

    public static <T> R<T> fail(HttpStatus status, String message) {
        return build(status.value(), message, null);
    }

    public static <T> R<T> fail(int code, String message) {
        return build(code, message, null);
    }

    /**
     * 通过 ResultCode 枚举快速构造失败响应。
     *
     * <p>个人博客项目采用三位数业务码 (详见 ResultCode), 此方法保持 API 调用简洁。
     */
    public static <T> R<T> fail(ResultCode resultCode) {
        return build(resultCode.getCode(), resultCode.getMessage(), null);
    }

    /**
     * ResultCode + 自定义消息的重载 (常用于更具体的错误提示)。
     */
    public static <T> R<T> fail(ResultCode resultCode, String message) {
        return build(resultCode.getCode(), message, null);
    }

    public static <T> R<T> build(int code, String message, T data) {
        R<T> r = new R<>();
        r.setCode(code);
        r.setMessage(message);
        r.setData(data);
        return r;
    }
}
