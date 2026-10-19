package com.xuyuan.blog.common.result;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 分页结果封装
 *
 * @param <T> 记录类型
 */
@Data
public class PageResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 当前页数据 */
    private List<T> records;

    /** 总记录数 */
    private long total;

    /** 当前页码 */
    private long page;

    /** 每页条数 */
    private long pageSize;

    /** 总页数 */
    private long pages;

    public static <T> PageResult<T> of(List<T> records, long total, long page, long pageSize) {
        PageResult<T> result = new PageResult<>();
        result.setRecords(records);
        result.setTotal(total);
        result.setPage(page);
        result.setPageSize(pageSize);
        result.setPages(pageSize > 0 ? (total + pageSize - 1) / pageSize : 0);
        return result;
    }
}
