package com.xuyuan.blog.admin.vo.moment;
import lombok.Data;
import java.io.Serializable;
import java.util.List;
@Data public class AdminMomentVO implements Serializable { private Long id; private String content; private List<String> images; private String location; private String mood; private Integer visible; private Long likeCount; private Long commentCount; private String createTime; }
