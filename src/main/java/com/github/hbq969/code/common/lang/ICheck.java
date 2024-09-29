package com.github.hbq969.code.common.lang;


import com.github.hbq969.code.common.spring.context.SpringContext;

import java.util.function.Supplier;

/**
 * @author : hbq969@gmail.com
 * @description : 检查接口
 * @createTime : 2023/8/30 17:40
 */
public interface ICheck {

    default void validCheck(SpringContext context) {
    }

    default void validCheck(SpringContext context, RuntimeException ex) {

    }
}
