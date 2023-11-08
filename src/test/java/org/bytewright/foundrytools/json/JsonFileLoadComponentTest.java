package org.bytewright.foundrytools.json;

import org.bytewright.foundrytools.config.AppSettingsTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.nio.file.Path;

@SpringBootTest
@ActiveProfiles("test")
class JsonFileLoadComponentTest {
    @Autowired
    JsonFileLoadComponent testee;
    @Autowired
    AppSettingsTest appSettingsTest;

    @Test
    void name() throws IOException {
        Path baseJsonSrcPath = appSettingsTest.getBaseJsonSrcPath();
        testee.doLoadAllFilesIn(baseJsonSrcPath);
    }
}