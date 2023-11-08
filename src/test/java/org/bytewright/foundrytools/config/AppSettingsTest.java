package org.bytewright.foundrytools.config;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.nio.file.Path;

@Service
@Primary
public class AppSettingsTest extends AppSettings{
    @Override
    public Path getBaseProjectPath() {
        return super.getBaseProjectPath().resolve("..");
    }
}
