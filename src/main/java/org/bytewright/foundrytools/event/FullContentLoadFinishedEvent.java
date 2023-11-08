package org.bytewright.foundrytools.event;

import org.springframework.context.ApplicationEvent;

public class FullContentLoadFinishedEvent extends ApplicationEvent {
    public FullContentLoadFinishedEvent(Object source) {
        super(source);
    }
}
