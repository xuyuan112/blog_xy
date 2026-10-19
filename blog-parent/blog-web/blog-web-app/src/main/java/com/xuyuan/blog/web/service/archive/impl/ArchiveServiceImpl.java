package com.xuyuan.blog.web.service.archive.impl;

import com.xuyuan.blog.web.mapper.ArticleMapper;
import com.xuyuan.blog.web.mapper.CategoryMapper;
import com.xuyuan.blog.web.mapper.TagMapper;
import com.xuyuan.blog.web.service.archive.ArchiveService;
import com.xuyuan.blog.web.vo.article.ArticleNavVO;
import com.xuyuan.blog.web.vo.misc.CategoryVO;
import com.xuyuan.blog.web.vo.misc.TagVO;
import com.xuyuan.blog.web.vo.timeline.TimelineMonthVO;
import com.xuyuan.blog.web.vo.timeline.TimelineYearVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArchiveServiceImpl implements ArchiveService {

    private final CategoryMapper categoryMapper;
    private final TagMapper tagMapper;
    private final ArticleMapper articleMapper;

    @Override
    public List<CategoryVO> categories() {
        return categoryMapper.listPublished();
    }

    @Override
    public List<TagVO> tags() {
        List<TagVO> result = tagMapper.listPublished();
        for (int i = 0; i < result.size(); i++) {
            result.get(i).setWeight(result.size() - i);
        }
        return result;
    }

    @Override
    public List<TimelineYearVO> timeline() {
        Map<String, List<ArticleNavVO>> byMonth = articleMapper.timeline().stream()
                .filter(article -> article.getPublishedAt() != null)
                .collect(Collectors.groupingBy(
                        article -> article.getPublishedAt().toString().substring(0, 7),
                        LinkedHashMap::new,
                        Collectors.toList()));

        Map<String, List<TimelineMonthVO>> byYear = new LinkedHashMap<>();
        byMonth.forEach((month, articles) -> {
            TimelineMonthVO monthVO = new TimelineMonthVO();
            monthVO.setMonth(month);
            monthVO.setArticles(articles);
            byYear.computeIfAbsent(month.substring(0, 4), key -> new ArrayList<>()).add(monthVO);
        });

        return byYear.entrySet().stream().map(entry -> {
            TimelineYearVO yearVO = new TimelineYearVO();
            yearVO.setYear(entry.getKey());
            yearVO.setMonths(entry.getValue());
            return yearVO;
        }).collect(Collectors.toList());
    }
}
