package com.xuyuan.blog.admin.vo.gallery;
import lombok.Data;
import java.io.Serializable;
@Data public class AdminPhotoVO implements Serializable { private Long id; private Long galleryId; private String original; private String thumbnail; private Integer sort; private String shootTime; }
