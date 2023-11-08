package org.bytewright.foundrytools.config;

import org.bytewright.foundrytools.event.DataExportFinishedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class AppShutdown implements ApplicationListener<DataExportFinishedEvent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(AppShutdown.class);

    @Autowired
    private ConfigurableApplicationContext ctx;

    @Override
    public void onApplicationEvent(DataExportFinishedEvent event) {
        LOGGER.info("After export is finished, shutting down app...");
        ctx.close();
        System.exit(0);
    }
}
