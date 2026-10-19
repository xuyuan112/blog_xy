package com.xuyuan.blog.web.vo.misc;

import com.xuyuan.blog.model.vo.MilestoneVO;
import com.xuyuan.blog.model.vo.SkillVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 关于我视图对象
 */
@Data
@Schema(description = "关于我(聚合技能树与履历里程碑)")
public class AboutVO implements Serializable {

    private static final long serialVersionUID = 1L;

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
    private List<SkillVO> skills;

    /** 履历里程碑 */
    @Schema(description = "履历里程碑列表")
    private List<MilestoneVO> milestones;
}
