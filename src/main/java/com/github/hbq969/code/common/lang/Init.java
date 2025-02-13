package com.github.hbq969.code.common.lang;

/**
 * @author : hbq969@gmail.com
 * @description : 初始化接口
 * @createTime : 2023/8/29 19:58
 */
public interface Init {

    default void init() {
    }

    default void finished(Runnable r) {
        r.run();
    }
}
