package org.bytewright.foundrytools.event;

import org.springframework.context.ApplicationEvent;

public class DataScrubbingFinishedEvent extends ApplicationEvent {
    public DataScrubbingFinishedEvent(Object object) {
        super(object);
    }
}
