package com.xuyuan.blog.model.handler;

import com.baomidou.mybatisplus.extension.handlers.AbstractJsonTypeHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Jackson 泛型 JSON 类型处理器基类
 *
 * <p>用于将 Java 对象与 MySQL JSON 类型字段自动互转,
 * 通过 {@link TypeReference} 保留泛型信息, 避免反序列化丢失具体类型。</p>
 *
 * @param <T> 字段对应 Java 类型
 */
public abstract class AbstractJacksonTypeHandler<T> extends AbstractJsonTypeHandler<T> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    protected T parse(String json) {
        try {
            return OBJECT_MAPPER.readValue(json, getTypeReference());
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON 反序列化失败: " + json, e);
        }
    }

    @Override
    protected String toJson(T obj) {
        try {
            return OBJECT_MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON 序列化失败", e);
        }
    }

    /**
     * 返回目标类型的 {@link TypeReference}, 由子类实现以保留泛型信息。
     */
    protected abstract TypeReference<T> getTypeReference();
}
