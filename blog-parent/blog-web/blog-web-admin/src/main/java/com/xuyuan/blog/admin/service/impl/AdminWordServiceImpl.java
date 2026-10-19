package com.xuyuan.blog.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuyuan.blog.admin.dto.WordCreateParam;
import com.xuyuan.blog.admin.mapper.WordMapper;
import com.xuyuan.blog.admin.service.AdminWordService;
import com.xuyuan.blog.admin.vo.misc.AdminWordVO;
import com.xuyuan.blog.common.result.PageResult;
import com.xuyuan.blog.common.result.ResultCode;
import com.xuyuan.blog.model.entity.Word;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminWordServiceImpl implements AdminWordService {

    private final WordMapper wordMapper;

    @Override
    public PageResult<AdminWordVO> pageList(long page, long size) {
        long pageSize = size > 0 && size <= 100 ? size : 10;
        Page<Word> result = wordMapper.selectPage(
                new Page<>(Math.max(1, page), pageSize),
                new LambdaQueryWrapper<Word>().orderByDesc(Word::getCreateTime));
        return PageResult.of(result.getRecords().stream().map(this::toVO).toList(),
                result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(WordCreateParam param) {
        Word word = new Word();
        BeanUtils.copyProperties(param, word);
        wordMapper.insert(word);
        return word.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, WordCreateParam param) {
        Word word = get(id);
        BeanUtils.copyProperties(param, word);
        wordMapper.updateById(word);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        wordMapper.deleteById(get(id).getId());
    }

    private Word get(Long id) {
        Word word = wordMapper.selectById(id);
        if (word == null) {
            throw ResultCode.PARAM_ERROR.toException("一言不存在");
        }
        return word;
    }

    private AdminWordVO toVO(Word word) {
        AdminWordVO vo = new AdminWordVO();
        BeanUtils.copyProperties(word, vo);
        return vo;
    }
}
