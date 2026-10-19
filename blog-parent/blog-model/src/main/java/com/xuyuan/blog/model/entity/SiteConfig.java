package com.xuyuan.blog.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xuyuan.blog.common.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 站点配置实体 (t_site_config)
 *
 * <p>键值对配置, 如 siteName / icp / notice。</p>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_site_config")
@Schema(description = "站点配置实体(键值对)")
public class SiteConfig extends BaseEntity {

    /** 配置键 */
    @Schema(description = "配置键", example = "siteName", requiredMode = Schema.RequiredMode.REQUIRED)
    private String configKey;

    /** 配置值 */
    @Schema(description = "配置值", example = "许袁的博客", requiredMode = Schema.RequiredMode.REQUIRED)
    private String configValue;

    /** 备注 */
    @Schema(description = "备注", example = "站点名称")
    private String remark;
}
