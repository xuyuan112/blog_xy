package com.xuyuan.blog.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xuyuan.blog.model.entity.Friend;
import com.xuyuan.blog.web.vo.friend.FriendVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface FriendMapper extends BaseMapper<Friend> {

    @Select("SELECT id,name,url,avatar,description,group_name AS groupName FROM t_friend WHERE deleted = 0 AND status = 1 AND (#{groupName} IS NULL OR group_name = #{groupName}) ORDER BY group_name ASC, sort ASC, id ASC")
    List<FriendVO> listApproved(@Param("groupName") String groupName);

    @Select("SELECT id,name,url,avatar,description,group_name AS groupName FROM t_friend WHERE deleted = 0 AND status = 1 ORDER BY RAND() LIMIT 1")
    FriendVO randomApproved();

    @Select("SELECT COUNT(1) FROM t_friend WHERE deleted = 0 AND (url = #{url} OR name = #{name}) AND status IN (0,1)")
    long countExisting(@Param("url") String url, @Param("name") String name);
}
