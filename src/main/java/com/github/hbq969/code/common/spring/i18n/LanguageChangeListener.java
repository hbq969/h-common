package com.github.hbq969.code.common.spring.i18n;

public interface LanguageChangeListener {
    void onChange(LangInfo langInfo);

    /**
     * 监听器优先级，值越小优先被执行
     *
     * @return
     */
    default int listenerOrder() {
        return 0;
    }

    String listenerName();
}
