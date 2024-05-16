package com.github.hbq969.code.common.spring.interceptor;

/**
 * @author : hbq969@gmail.com
 * @description : springmvc拦截排序接口
 * @createTime : 2023/8/27 13:16
 */
public interface InterceptorOrder {

    /**
     * 执行优先级，数字越小优先级最高<br>
     * 使用大于等于0的值，不要使用小于0的值
     *
     * @return
     */
    default int order() {
        return 0;
    }
}
