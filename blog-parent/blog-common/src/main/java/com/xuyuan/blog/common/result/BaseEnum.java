package com.xuyuan.blog.common.result;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 通用枚举接口
 *
 * <p>所有业务状态枚举实现该接口, 提供 code 与 label 双值映射。
 * 数据库存储 {@link #getCode()}, 前端展示 {@link #getLabel()}。</p>
 *
 * <p>JSON 序列化: {@link #getCode()} 标注 {@link JsonValue @JsonValue},
 * 枚举序列化为 code 值(而非默认的 name())。</p>
 *
 * @param <T> code 类型(Integer 或 String)
 */
public interface BaseEnum<T> {

    /**
     * 数据库存储值
     *
     * <p>同时作为 JSON 序列化值。</p>
     */
    @JsonValue
    T getCode();

    /** 前端展示文本 */
    String getLabel();

    /**
     * 按 code 反查枚举值
     *
     * @param enumType 枚举类
     * @param code     code 值
     * @param <E>      枚举类型
     * @param <C>      code 类型
     * @return 匹配的枚举值, 未找到返回 null
     */
    static <E extends BaseEnum<?>, C> E of(Class<E> enumType, C code) {
        if (code == null) {
            return null;
        }
        for (E e : enumType.getEnumConstants()) {
            if (e.getCode().equals(code)) {
                return e;
            }
        }
        return null;
    }
}
