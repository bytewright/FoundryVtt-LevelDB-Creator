package org.bytewright.foundrytools.leveldb;

import org.bytewright.foundrytools.config.AppSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class LevelDBExporterFactory {
    @Autowired
    private AppSettings appSettings;
    @Autowired
    private LevelDbIdPrefixer idPrefixer;

    public LevelDBExporter create() {
        return new LevelDBExporter(appSettings, idPrefixer);
    }
}
