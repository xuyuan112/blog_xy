package com.xuyuan.blog.admin.dto;
import jakarta.validation.constraints.NotBlank;import lombok.Data;import java.io.Serializable;import java.time.LocalDateTime;
@Data public class ScheduleCreateParam implements Serializable { @NotBlank private String title; private LocalDateTime targetTime; @NotBlank private String type; }
