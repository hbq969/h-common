package com.github.hbq969.code.common.utils;

import com.google.common.collect.Lists;
import lombok.Data;

import java.util.Collections;
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

    public static <T> List<T> subListWithOneFirst(List<T> parent, int page, int size) {
        int s = (page - 1) * size;
        if (s > parent.size()) {
            return Collections.emptyList();
        }
        int e = page * size;
        if (e > parent.size()) {
            e = parent.size();
        }
        return parent.subList(s, e);
    }

    public static <T> List<T> subListWithZeroFirst(List<T> parent, int page, int size) {
        int s = page * size;
        if (s > parent.size()) {
            return Collections.emptyList();
        }
        int e = (page + 1) * size;
        if (e > parent.size()) {
            e = parent.size();
        }
        return parent.subList(s, e);
    }

    public static void main(String[] args) {
        List<String> list = Lists.newArrayList("foo", "nar");
        System.out.println(subListWithZeroFirst(list, 1, 2));
    }
}
