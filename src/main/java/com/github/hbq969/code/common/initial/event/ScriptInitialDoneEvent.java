package com.github.hbq969.code.common.initial.event;

import org.springframework.context.ApplicationEvent;

public class ScriptInitialDoneEvent extends ApplicationEvent {
    public ScriptInitialDoneEvent(Object source) {
        super(source);
    }
}
