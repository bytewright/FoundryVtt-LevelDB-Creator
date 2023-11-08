package org.bytewright.foundrytools.leveldb;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bytewright.foundrytools.event.DataExportFinishedEvent;
import org.bytewright.foundrytools.event.DataExportReadyEvent;
import org.bytewright.foundrytools.json.FoundryPack;
import org.bytewright.foundrytools.json.JsonStorage;
import org.bytewright.foundrytools.json.PackNameExtractor;
import org.bytewright.foundrytools.json.pojo.BaseFoundryVttObject;
import org.bytewright.foundrytools.json.pojo.FoundryDbObject;
import org.bytewright.foundrytools.util.IdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class DataExportService implements ApplicationListener<DataExportReadyEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataExportService.class);
    @Autowired
    ObjectMapper mapper;
    @Autowired
    private LevelDBExporterFactory levelDBExporterFactory;
    @Autowired
    private JsonStorage jsonStorage;
    @Autowired
    private IdGenerator idGenerator;
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Override
    public void onApplicationEvent(DataExportReadyEvent event) {
        LOGGER.info("Data is ready for export, starting to export all data to leveldb...");
        try {
            removeFolderFlags();
            Set<String> packNames = jsonStorage.getPackNames();
            LevelDBExporter exporter = levelDBExporterFactory.create();
            if (exporter.initialize(packNames)) {
                loadDataIntoLevelDB(exporter, packNames);
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadDataIntoLevelDB(LevelDBExporter exporter, Set<String> packNames) throws JsonProcessingException {
        for (String packName : packNames) {
            FoundryPack foundryPack = jsonStorage.getPack(packName);
            foundryPack.exportFolders().map(this::serialize).forEach(s -> exporter.add(packName, s));
            foundryPack.exportContent().map(this::serialize).forEach(s -> exporter.add(packName, s));
            try {
                exporter.commitPack(packName);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            LOGGER.info("Pack {} loaded to LevelDB!", packName);
        }
        LOGGER.info("All {} packs in in-memory storage are exported to LevelDB", packNames.size());
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.schedule(new ContentLoadFinishedRunnable(eventPublisher, exporter), 3, TimeUnit.SECONDS);
    }

    private void removeFolderFlags() {
        jsonStorage.streamAllItems()
                .filter(systemItem -> systemItem.getFlagsMap() != null)
                .forEach(systemItem -> systemItem.getFlagsMap().computeIfPresent(PackNameExtractor.FOLDER_FLAG, (s, o) -> null));
    }

    private SerializedFoundryObj serialize(BaseFoundryVttObject foundryDbObject) {
        try {
            return new SerializedFoundryObj(foundryDbObject, mapper.writeValueAsString(foundryDbObject));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private static class ContentLoadFinishedRunnable implements Runnable {
        private final ApplicationEventPublisher eventPublisher;
        private final LevelDBExporter exporter;

        public ContentLoadFinishedRunnable(ApplicationEventPublisher eventPublisher, LevelDBExporter exporter) {
            this.eventPublisher = eventPublisher;
            this.exporter = exporter;
        }

        @Override
        public void run() {
            exporter.contentLoadFinished();
            eventPublisher.publishEvent(new DataExportFinishedEvent(this));
        }
    }
}
