package com.xuyuan.blog.admin.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuyuan.blog.admin.dto.MomentCreateParam;
import com.xuyuan.blog.admin.dto.MomentQueryParam;
import com.xuyuan.blog.admin.mapper.MomentMapper;
import com.xuyuan.blog.admin.service.AdminMomentService;
import com.xuyuan.blog.admin.vo.moment.AdminMomentVO;
import com.xuyuan.blog.common.result.PageResult;
import com.xuyuan.blog.common.result.ResultCode;
import com.xuyuan.blog.model.entity.Moment;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminMomentServiceImpl implements AdminMomentService {

    private final MomentMapper momentMapper;

    @Override
    public PageResult<AdminMomentVO> pageList(MomentQueryParam param) {
        long page = Math.max(1, param.getPage());
        long pageSize = param.getPageSize() > 0 && param.getPageSize() <= 100 ? param.getPageSize() : 10;
        IPage<AdminMomentVO> result = momentMapper.pageAdminList(new Page<>(page, pageSize), param);
        return PageResult.of(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(MomentCreateParam param) {
        validateImages(param);
        Moment moment = new Moment();
        BeanUtils.copyProperties(param, moment);
        momentMapper.insert(moment);
        return moment.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, MomentCreateParam param) {
        validateImages(param);
        Moment moment = momentMapper.selectById(id);
        if (moment == null) {
            throw ResultCode.ADMIN_MOMENT_NOT_FOUND.toException();
        }
        BeanUtils.copyProperties(param, moment);
        momentMapper.updateById(moment);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        if (momentMapper.selectById(id) == null) {
            throw ResultCode.ADMIN_MOMENT_NOT_FOUND.toException();
        }
        momentMapper.deleteById(id);
    }

    private void validateImages(MomentCreateParam param) {
        if (param.getImages() != null && param.getImages().size() > 9) {
            throw ResultCode.PARAM_ERROR.toException();
        }
    }
}
