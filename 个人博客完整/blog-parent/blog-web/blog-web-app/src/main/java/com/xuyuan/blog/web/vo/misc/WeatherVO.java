package com.xuyuan.blog.web.vo.misc;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 天气视图对象
 */
@Data
@Schema(description = "天气信息(首页小组件)")
public class WeatherVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 城市 */
    @Schema(description = "城市", example = "杭州")
    private String city;

    /** 温度 */
    @Schema(description = "温度", example = "28℃")
    private String temp;

    /** 天气现象 */
    @Schema(description = "天气现象", example = "晴")
    private String weather;

    /** 天气图标标识 */
    @Schema(description = "天气图标标识", example = "sunny")
    private String icon;
}
