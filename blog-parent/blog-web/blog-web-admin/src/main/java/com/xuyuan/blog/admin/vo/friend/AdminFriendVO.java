package com.xuyuan.blog.admin.vo.friend;

import lombok.Data;
import java.io.Serializable;

@Data
public class AdminFriendVO implements Serializable {
    private Long id;
    private String name;
    private String url;
    private String avatar;
    private String description;
    private String group;
    private String email;
    private Integer status;
    private Integer sort;
    private String createTime;
    private String updateTime;
}
