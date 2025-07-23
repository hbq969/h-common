package com.github.hbq969.code.common.initial;

import com.github.hbq969.code.common.spring.i18n.LanguageEvent;
import org.springframework.context.ApplicationListener;

import java.util.concurrent.TimeUnit;

public interface ScriptInitialAware extends ApplicationListener<LanguageEvent> {
    /**
     * 定义脚本初始化适配器名称
     *
     * @return
     */
    String nameOfScriptInitialAware();

    /**
     * 执行优先级，越小越高
     *
     * @return
     */
    default int orderOfScriptInitialAware() {
        return 0;
    }

    /**
     * 创建表
     */
    void tableCreate();

    default void tableCreateDone(long timeout, TimeUnit unit) {
    }

    default void asyncTableCreateDone(long timeout, TimeUnit unit, Runnable r) {

    }


    /**
     * 初始化脚本内容
     */
    void scriptInitial();

    default void scriptInitialDone(long timeout, TimeUnit unit) {
    }

    default void asyncScriptInitialDone(long timeout, TimeUnit unit, Runnable r) {
    }
}
