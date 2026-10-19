package com.xuyuan.blog.web.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xuyuan.blog.model.entity.*;
import org.apache.ibatis.annotations.Mapper; import org.apache.ibatis.annotations.Select;
import java.util.List;
@Mapper
public interface SiteMapper {
    @Select("SELECT id,content,update_time AS updatedAt FROM t_notice WHERE deleted=0 ORDER BY update_time DESC,id DESC LIMIT 1") Notice latestNotice();
    @Select("SELECT content,source FROM t_word WHERE deleted=0 ORDER BY RAND() LIMIT 1") Word randomWord();
    @Select("SELECT id,title,target_time AS targetTime,type FROM t_schedule WHERE deleted=0 AND target_time >= NOW() ORDER BY target_time ASC") List<Schedule> schedules();
}
