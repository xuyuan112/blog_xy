package com.xuyuan.blog.model.handler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.xuyuan.blog.model.vo.ExifVO;

/**
 * EXIF 参数 JSON 类型处理器
 *
 * <p>对应 t_photo.exif 字段, 将 ExifVO 对象与 JSON 互转。</p>
 */
public class ExifTypeHandler extends AbstractJacksonTypeHandler<ExifVO> {

    @Override
    protected TypeReference<ExifVO> getTypeReference() {
        return new TypeReference<ExifVO>() {
        };
    }
}
