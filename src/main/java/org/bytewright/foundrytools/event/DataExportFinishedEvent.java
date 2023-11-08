package org.bytewright.foundrytools.event;

import org.springframework.context.ApplicationEvent;

public class DataExportFinishedEvent extends ApplicationEvent {
    public DataExportFinishedEvent(Object source) {
        super(source);
    }
}
