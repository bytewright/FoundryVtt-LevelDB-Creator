package org.bytewright.foundrytools.config;

import org.bytewright.foundrytools.json.JsonFileLoadComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Primary;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

@Service
@Primary
public class ChainStarter implements ApplicationListener<ContextRefreshedEvent> {
    @Autowired
    private JsonFileLoadComponent jsonFileLoadComponent;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        //do nothing
    }
}
