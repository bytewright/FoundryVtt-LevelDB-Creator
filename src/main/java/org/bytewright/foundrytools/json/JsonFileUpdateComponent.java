package org.bytewright.foundrytools.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.bytewright.foundrytools.config.AppSettings;
import org.bytewright.foundrytools.event.DataExportReadyEvent;
import org.bytewright.foundrytools.event.DataScrubbingFinishedEvent;
import org.bytewright.foundrytools.json.pojo.BaseFoundryVttObject;
import org.bytewright.foundrytools.json.pojo.CompendiumFolderItem;
import org.bytewright.foundrytools.util.JacksonPrettyPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

@Service
public class JsonFileUpdateComponent implements ApplicationListener<DataScrubbingFinishedEvent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonFileUpdateComponent.class);
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    @Autowired
    private JsonStorage jsonStorage;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private AppSettings appSettings;

    @Override
    public void onApplicationEvent(DataScrubbingFinishedEvent event) {
        LOGGER.info("Data scrubbing is done, writing changes to json files...");
        jsonStorage.streamAllItems()
                .forEach(this::writeFileIfModified);

        eventPublisher.publishEvent(new DataExportReadyEvent(this));
    }

    private void writeFileIfModified(BaseFoundryVttObject foundryDbObject) {
        try {
            ObjectWriter prettyPrinter = mapper.writer(JacksonPrettyPrinter.INSTANCE);
            String json = prettyPrinter.writeValueAsString(foundryDbObject);
            Path srcPath = foundryDbObject.getSrcPath();
            if (srcPath == null && foundryDbObject instanceof CompendiumFolderItem) {
                LOGGER.debug("obj has null path but is dir so okay, {}", foundryDbObject);
            } else if (srcPath == null) {
                LOGGER.error("obj has null path, {}", foundryDbObject);
            } else if (Files.isRegularFile(srcPath) && Files.isWritable(srcPath)) {
                Files.writeString(srcPath, json, StandardOpenOption.TRUNCATE_EXISTING);
            } else if (!Files.exists(srcPath)) {
                LOGGER.info("New Json created in this run: {}", srcPath.toAbsolutePath());
                Files.writeString(srcPath, json, StandardOpenOption.CREATE_NEW);
            } else {
                LOGGER.warn("Src path from obj looks wrong! {}", foundryDbObject);
            }
        } catch (JsonProcessingException e) {
            LOGGER.error("Failed to serialize: {}", foundryDbObject, e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
