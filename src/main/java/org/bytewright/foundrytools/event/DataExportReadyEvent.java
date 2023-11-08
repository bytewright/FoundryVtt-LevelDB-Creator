package org.bytewright.foundrytools.event;

import org.springframework.context.ApplicationEvent;

public class DataExportReadyEvent extends ApplicationEvent {
    public DataExportReadyEvent(Object source) {
        super(source);
    }
}
