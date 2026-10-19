package com.xuyuan.blog.model.handler;

import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;

/**
 * 字符串数组 JSON 类型处理器
 *
 * <p>对应 t_moment.images 字段, 将 List&lt;String&gt; 与 JSON 数组互转。</p>
 */
public class StringListTypeHandler extends AbstractJacksonTypeHandler<List<String>> {

    @Override
    protected TypeReference<List<String>> getTypeReference() {
        return new TypeReference<List<String>>() {
        };
    }
}
