package com.github.hbq969.code.common.spring.interceptor;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * @author : hbq969@gmail.com
 * @description : 拦截器影响路径适配接口
 * @createTime : 2023/11/22 17:46
 */
public interface PathPatternsAware {
    
    /**
     * 返回拦截器适用的uri，不含<code>server.servlet.context-path</code>指定的部分<br>
     * 匹配逻辑 比如 ? 匹配单个字符，* 匹配零个或多个字符，** 匹配零个或多个目录
     *
     * @return
     */
    default List<String> getPathPatterns() {
        return Lists.newArrayList("/**");
    }

    /**
     * 返回需要排除的uri，使用正则匹配<br>
     * 匹配逻辑 比如 ? 匹配单个字符，* 匹配零个或多个字符，** 匹配零个或多个目录
     *
     * @return
     */
    default List<String> getExcludedPathPatterns() {
        return null;
    }
}
