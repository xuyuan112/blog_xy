package com.xuyuan.blog.admin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 说说发布/编辑请求参数
 */
@Data
public class MomentCreateParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 说说内容 */
    @NotBlank(message = "内容不能为空")
    private String content;

    /** 图片数组(最多 9 张) */
    private List<String> images;

    /** 发布定位 */
    private String location;

    /** 心情 Tag */
    private String mood;

    /** 可见性: 0 公开, 1 隐藏, 2 私密 */
    private Integer visible;
}
