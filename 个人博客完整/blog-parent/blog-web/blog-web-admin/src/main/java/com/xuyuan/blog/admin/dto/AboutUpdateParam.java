package com.xuyuan.blog.admin.dto;
import com.xuyuan.blog.model.vo.MilestoneVO;import com.xuyuan.blog.model.vo.SkillVO;import lombok.Data;import java.io.Serializable;import java.util.List;
@Data public class AboutUpdateParam implements Serializable { private String nickname; private String avatar; private String signature; private String intro; private List<SkillVO> skills; private List<MilestoneVO> milestones; }
