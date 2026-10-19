package com.xuyuan.blog.admin.dto;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.io.Serializable;
@Data public class NoticeCreateParam implements Serializable { @NotBlank private String content; }
