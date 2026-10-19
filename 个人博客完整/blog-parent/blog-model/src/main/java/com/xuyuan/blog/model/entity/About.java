package com.xuyuan.blog.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xuyuan.blog.common.entity.BaseEntity;
import com.xuyuan.blog.model.handler.MilestoneListTypeHandler;
import com.xuyuan.blog.model.handler.SkillListTypeHandler;
import com.xuyuan.blog.model.vo.MilestoneVO;
import com.xuyuan.blog.model.vo.SkillVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 关于我实体 (t_about)
 *
 * <p>单行配置表。</p>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "t_about", autoResultMap = true)
@Schema(description = "关于我实体(单行配置表)")
public class About extends BaseEntity {

    /** 昵称 */
    @Schema(description = "昵称", example = "许袁")
    private String nickname;

    /** 头像地址 */
    @Schema(description = "头像地址(URL)", example = "https://example.com/avatar.png")
    private String avatar;

    /** 个性签名 */
    @Schema(description = "个性签名", example = "代码改变世界")
    private String signature;

    /** 自我介绍 */
    @Schema(description = "自我介绍(Markdown)")
    private String intro;

    /** 技能树 */
    @Schema(description = "技能树列表")
    @TableField(typeHandler = SkillListTypeHandler.class)
    private List<SkillVO> skills;

    /** 履历里程碑 */
    @Schema(description = "履历里程碑列表")
    @TableField(typeHandler = MilestoneListTypeHandler.class)
    private List<MilestoneVO> milestones;
}
