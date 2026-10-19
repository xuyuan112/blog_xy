package com.xuyuan.blog.admin.dto;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.io.Serializable;
@Data public class WordCreateParam implements Serializable { @NotBlank private String content; private String source; }
