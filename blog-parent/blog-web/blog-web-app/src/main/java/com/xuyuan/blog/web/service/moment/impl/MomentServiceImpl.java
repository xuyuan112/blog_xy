package com.xuyuan.blog.web.service.moment.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuyuan.blog.common.result.PageResult;
import com.xuyuan.blog.model.entity.Moment;
import com.xuyuan.blog.web.dto.moment.MomentPageQuery;
import com.xuyuan.blog.web.mapper.MomentMapper;
import com.xuyuan.blog.web.service.moment.MomentService;
import com.xuyuan.blog.web.vo.moment.MomentVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MomentServiceImpl implements MomentService {

    private final MomentMapper momentMapper;

    @Override
    public PageResult<MomentVO> page(MomentPageQuery query) {
        normalize(query);
        IPage<MomentVO> result = momentMapper.pagePublished(
                new Page<>(query.getPage(), query.getPageSize()), query);
        return PageResult.of(result.getRecords(), result.getTotal(),
                result.getCurrent(), result.getSize());
    }

    @Override
    public MomentVO detail(Long id) {
        Moment moment = momentMapper.selectById(id);
        if (moment == null || !Integer.valueOf(0).equals(moment.getVisible())
                || Integer.valueOf(1).equals(moment.getDeleted())) {
            return null;
        }
        MomentVO result = new MomentVO();
        BeanUtils.copyProperties(moment, result);
        result.setCreatedAt(moment.getCreateTime());
        return result;
    }

    private void normalize(MomentPageQuery query) {
        if (query.getPage() == null || query.getPage() < 1) {
            query.setPage(1);
        }
        if (query.getPageSize() == null || query.getPageSize() < 1) {
            query.setPageSize(10);
        }
        query.setPageSize(Math.min(query.getPageSize(), 50));
    }
}
