package com.github.hbq969.code.common.lang;

import com.github.hbq969.code.common.spring.context.SpringContext;

/**
 * @author : hbq969@gmail.com
 * @description : 转换器
 * @createTime : 2024/10/19 10:20
 */
public interface Converter<T> {
    T convert(SpringContext context);
}
