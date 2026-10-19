package com.xuyuan.blog.common.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 实体基类
 *
 * <p>统一封装通用审计字段, 所有业务实体继承此类。
 * create_time / update_time 由 MyBatis-Plus MetaObjectHandler 自动填充,
 * deleted 为逻辑删除标记, 由 @TableLogic 维护。</p>
 */
@Data
@Schema(description = "实体基类(含通用审计字段)")
public abstract class BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键 ID */
    @Schema(description = "主键 ID", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 创建时间 */
    @Schema(description = "创建时间", accessMode = Schema.AccessMode.READ_ONLY)
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间 */
    @Schema(description = "更新时间", accessMode = Schema.AccessMode.READ_ONLY)
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /** 创建人 */
    @Schema(description = "创建人 ID", accessMode = Schema.AccessMode.READ_ONLY)
    @TableField(fill = FieldFill.INSERT)
    private Long createBy;

    /** 更新人 */
    @Schema(description = "更新人 ID", accessMode = Schema.AccessMode.READ_ONLY)
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateBy;

    /** 逻辑删除: 0 否, 1 是 */
    @Schema(description = "逻辑删除标记(0 否, 1 是)", accessMode = Schema.AccessMode.READ_ONLY)
    @TableLogic(value = "0", delval = "1")
    private Integer deleted = 0;
}
