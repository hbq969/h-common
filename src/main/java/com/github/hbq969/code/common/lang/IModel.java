package com.github.hbq969.code.common.lang;

import com.github.hbq969.code.common.spring.context.SpringContext;

/**
 * @author : hbq969@gmail.com
 * @description :
 * @createTime : 2024/5/14 16:05
 */
public interface IModel<T> {
    T ct(SpringContext context);
}
