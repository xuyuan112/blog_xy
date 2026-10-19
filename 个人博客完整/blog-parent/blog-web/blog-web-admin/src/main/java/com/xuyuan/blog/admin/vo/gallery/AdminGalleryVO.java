package com.xuyuan.blog.admin.vo.gallery;
import lombok.Data;
import java.io.Serializable;
@Data public class AdminGalleryVO implements Serializable { private Long id; private String name; private String description; private String cover; private String shootDate; private Integer sort; private Long photoCount; private String createTime; }
