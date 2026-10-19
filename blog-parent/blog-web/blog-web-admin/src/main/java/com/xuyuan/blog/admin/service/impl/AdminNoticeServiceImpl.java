package com.xuyuan.blog.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuyuan.blog.admin.dto.NoticeCreateParam;
import com.xuyuan.blog.admin.mapper.NoticeMapper;
import com.xuyuan.blog.admin.service.AdminNoticeService;
import com.xuyuan.blog.admin.vo.misc.AdminNoticeVO;
import com.xuyuan.blog.common.result.PageResult;
import com.xuyuan.blog.common.result.ResultCode;
import com.xuyuan.blog.model.entity.Notice;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminNoticeServiceImpl implements AdminNoticeService {

    private final NoticeMapper noticeMapper;

    @Override
    public PageResult<AdminNoticeVO> pageList(long page, long size) {
        long current = Math.max(1, page);
        long pageSize = size > 0 && size <= 100 ? size : 10;
        LambdaQueryWrapper<Notice> wrapper = new LambdaQueryWrapper<Notice>()
                .orderByDesc(Notice::getCreateTime);
        Page<Notice> result = noticeMapper.selectPage(new Page<>(current, pageSize), wrapper);
        return PageResult.of(
                result.getRecords().stream().map(this::toVO).toList(),
                result.getTotal(),
                result.getCurrent(),
                result.getSize());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(NoticeCreateParam param) {
        Notice notice = new Notice();
        BeanUtils.copyProperties(param, notice);
        noticeMapper.insert(notice);
        return notice.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, NoticeCreateParam param) {
        Notice notice = get(id);
        notice.setContent(param.getContent());
        noticeMapper.updateById(notice);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        noticeMapper.deleteById(get(id).getId());
    }

    private Notice get(Long id) {
        Notice notice = noticeMapper.selectById(id);
        if (notice == null) {
            throw ResultCode.PARAM_ERROR.toException("公告不存在");
        }
        return notice;
    }

    private AdminNoticeVO toVO(Notice notice) {
        AdminNoticeVO vo = new AdminNoticeVO();
        BeanUtils.copyProperties(notice, vo);
        return vo;
    }
}
