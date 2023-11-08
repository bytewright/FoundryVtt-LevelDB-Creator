package org.bytewright.foundrytools.config;

import org.bytewright.foundrytools.json.JsonFileLoadComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
@Profile("default")
public class ChainStarter implements ApplicationListener<ContextRefreshedEvent> {
    @Autowired
    private JsonFileLoadComponent jsonFileLoadComponent;
    @Autowired
    private Environment environment;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        for (String profileName : environment.getActiveProfiles()) {
            System.out.println("Currently active profile - " + profileName);
        }
        jsonFileLoadComponent.start();
    }
}
