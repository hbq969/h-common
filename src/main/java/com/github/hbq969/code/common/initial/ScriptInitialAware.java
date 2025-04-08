package com.github.hbq969.code.common.initial;

import com.github.hbq969.code.common.spring.i18n.LanguageEvent;
import org.springframework.context.ApplicationListener;

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

    /**
     * 初始化脚本内容
     */
    void scriptInitial();
}
