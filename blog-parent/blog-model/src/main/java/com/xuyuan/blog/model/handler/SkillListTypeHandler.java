package com.xuyuan.blog.model.handler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.xuyuan.blog.model.vo.SkillVO;

import java.util.List;

/**
 * 技能树 JSON 数组类型处理器
 *
 * <p>对应 t_about.skills 字段, 将 List&lt;SkillVO&gt; 与 JSON 数组互转。</p>
 */
public class SkillListTypeHandler extends AbstractJacksonTypeHandler<List<SkillVO>> {

    @Override
    protected TypeReference<List<SkillVO>> getTypeReference() {
        return new TypeReference<List<SkillVO>>() {
        };
    }
}
