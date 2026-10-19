package com.xuyuan.blog.model.handler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.xuyuan.blog.model.vo.MilestoneVO;

import java.util.List;

/**
 * 里程碑 JSON 数组类型处理器
 *
 * <p>对应 t_about.milestones 字段, 将 List&lt;MilestoneVO&gt; 与 JSON 数组互转。</p>
 */
public class MilestoneListTypeHandler extends AbstractJacksonTypeHandler<List<MilestoneVO>> {

    @Override
    protected TypeReference<List<MilestoneVO>> getTypeReference() {
        return new TypeReference<List<MilestoneVO>>() {
        };
    }
}
