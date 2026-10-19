package com.xuyuan.blog.common.exception;

import com.xuyuan.blog.common.result.R;
import com.xuyuan.blog.common.result.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 *
 * <p>统一将异常转换为 {@link R} 结构返回, 保证响应格式一致。
 * 业务码遵循 {@link ResultCode} 三位数约定, HTTP 响应行统一 200。</p>
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** 业务异常 */
    @ExceptionHandler(BusinessException.class)
    public R<Void> handleBusinessException(BusinessException e) {
        log.warn("业务异常: {}", e.getMessage());
        return R.fail(e.getCode(), e.getMessage());
    }

    /** 参数校验异常 (@RequestBody + @Valid) */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public R<Void> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        String message = resolveFieldError(e.getBindingResult().getFieldError());
        return ResultCode.PARAM_ERROR.toR(message);
    }

    /** 参数绑定异常 */
    @ExceptionHandler(BindException.class)
    public R<Void> handleBindException(BindException e) {
        String message = resolveFieldError(e.getFieldError());
        return ResultCode.PARAM_ERROR.toR(message);
    }

    /** 兜底异常 */
    @ExceptionHandler(Exception.class)
    public R<Void> handleException(Exception e) {
        log.error("系统异常", e);
        return ResultCode.SERVICE_ERROR.toR();
    }

    private String resolveFieldError(FieldError fieldError) {
        if (fieldError == null) {
            return "请求参数错误";
        }
        return fieldError.getDefaultMessage() != null
                ? fieldError.getDefaultMessage()
                : fieldError.getField() + " 参数错误";
    }
}
