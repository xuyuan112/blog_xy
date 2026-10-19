package com.xuyuan.blog.web.service.about.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuyuan.blog.model.entity.About;
import com.xuyuan.blog.model.vo.MilestoneVO;
import com.xuyuan.blog.model.vo.SkillVO;
import com.xuyuan.blog.web.mapper.AboutMapper;
import com.xuyuan.blog.web.service.about.AboutService;
import com.xuyuan.blog.web.vo.misc.AboutVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AboutServiceImpl implements AboutService {

    private final AboutMapper aboutMapper;

    @Override
    public AboutVO get() {
        About about = aboutMapper.selectOne(new LambdaQueryWrapper<About>()
                .eq(About::getDeleted, 0)
                .orderByDesc(About::getUpdateTime)
                .last("LIMIT 1"));
        AboutVO result = new AboutVO();
        if (about == null) {
            result.setSkills(Collections.emptyList());
            result.setMilestones(Collections.emptyList());
            return result;
        }
        BeanUtils.copyProperties(about, result);
        result.setSkills(sortedSkills(about.getSkills()));
        result.setMilestones(sortedMilestones(about.getMilestones()));
        return result;
    }

    private List<SkillVO> sortedSkills(List<SkillVO> skills) {
        if (skills == null) {
            return Collections.emptyList();
        }
        return skills.stream()
                .filter(skill -> skill != null && skill.getName() != null)
                .peek(skill -> skill.setLevel(Math.max(0, Math.min(100,
                        skill.getLevel() == null ? 0 : skill.getLevel()))))
                .sorted(Comparator.comparing(SkillVO::getCategory,
                        Comparator.nullsLast(String::compareToIgnoreCase)))
                .toList();
    }

    private List<MilestoneVO> sortedMilestones(List<MilestoneVO> milestones) {
        if (milestones == null) {
            return Collections.emptyList();
        }
        return milestones.stream()
                .filter(milestone -> milestone != null && milestone.getTitle() != null)
                .sorted(Comparator.comparing(MilestoneVO::getYear,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();
    }
}
