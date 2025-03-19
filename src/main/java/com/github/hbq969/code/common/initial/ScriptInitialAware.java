package com.github.hbq969.code.common.initial;

public interface ScriptInitialAware {
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
