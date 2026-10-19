package com.xuyuan.blog.admin.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuyuan.blog.admin.dto.MomentQueryParam;
import com.xuyuan.blog.admin.vo.moment.AdminMomentVO;
import com.xuyuan.blog.model.entity.Moment;
import org.apache.ibatis.annotations.Param;
public interface MomentMapper extends BaseMapper<Moment> { IPage<AdminMomentVO> pageAdminList(Page<AdminMomentVO> page,@Param("param") MomentQueryParam param); }
