package com.github.hbq969.code.common.spring.i18n;

import org.springframework.context.ApplicationEvent;

public class LanguageEvent extends ApplicationEvent {
    private LangInfo langInfo;

    public LanguageEvent(LangInfo langInfo) {
        super(langInfo);
    }
}
