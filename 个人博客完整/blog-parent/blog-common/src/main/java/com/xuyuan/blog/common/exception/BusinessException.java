package com.xuyuan.blog.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 业务异常
 *
 * <p>通过抛出该异常, 由全局异常处理器统一转换为 {@link com.xuyuan.blog.common.result.R} 响应。</p>
 */
@Getter
public class BusinessException extends RuntimeException {

    private final int code;

    public BusinessException(String message) {
        super(message);
        this.code = HttpStatus.INTERNAL_SERVER_ERROR.value();
    }

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(HttpStatus status, String message) {
        super(message);
        this.code = status.value();
    }
}
