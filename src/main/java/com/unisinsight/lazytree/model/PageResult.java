package com.unisinsight.lazytree.model;

import lombok.Getter;

import java.util.List;

@Getter
public class PageResult<T> {
    private List<T> data;
    private int pageSize;
    private int pageNum;
    private int total;

    public static  <T> PageResult of(List<T> data, int pageSize, int pageNum, int total) {
        PageResult result = new PageResult();
        result.data = data;
        result.pageSize = pageSize;
        result.pageNum = pageNum;
        result.total = total;

        return result;
    }
}
