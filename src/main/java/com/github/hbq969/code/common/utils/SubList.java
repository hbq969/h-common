package com.github.hbq969.code.common.utils;

import lombok.Data;

import java.util.List;

/**
 * @author : hbq969@gmail.com
 * @description : 子集合包装器
 * @createTime : 15:04:21, 2023.03.31, 周五
 */
@Data
public class SubList<T> {

    private Offset offset;
    private List<T> list;

    public int getBeginIntValue() {
        return offset.getBeginIntValue();
    }

    public int getEndIntValue() {
        return offset.getEndIntValue();
    }

    public long getBeginLongValue() {
        return offset.getBeginLongValue();
    }

    public long getEndLongValue() {
        return offset.getEndLongValue();
    }
}
