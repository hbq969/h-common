package com.github.hbq969.code.common.initial.event;

import org.springframework.context.ApplicationEvent;

public class TableCreateDoneEvent extends ApplicationEvent {
    public TableCreateDoneEvent(Object source) {
        super(source);
    }
}
