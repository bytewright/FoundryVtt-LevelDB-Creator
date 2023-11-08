package org.bytewright.foundrytools.json;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.annotations.VisibleForTesting;
import org.bytewright.foundrytools.config.AppSettings;
import org.bytewright.foundrytools.event.FullContentLoadFinishedEvent;
import org.bytewright.foundrytools.json.jackson.FoundryObjMapper;
import org.bytewright.foundrytools.json.pojo.BaseFoundryVttObject;
import org.bytewright.foundrytools.util.IdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

@Component
public class JsonFileLoadComponent {
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonFileLoadComponent.class);
    @Autowired
    private FoundryObjMapper foundryObjMapper;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private JsonStorage jsonStorage;
    @Autowired
    private AppSettings appSettings;
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    @Autowired
    private PackNameExtractor packNameExtractor;
    @Autowired
    private IdGenerator idGenerator;

    public void start() {
        Path path = appSettings.getBaseJsonSrcPath();
        LOGGER.info("Starting to load jsons from path: {}", path);
        try {
            loadJsonFiles(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadJsonFiles(Path path) {
        try {
            doLoadAllFilesIn(path);
            LOGGER.info("Found and loaded {} json files into these packs: {}", jsonStorage.totalSize(), jsonStorage.getPackNames());
            eventPublisher.publishEvent(new FullContentLoadFinishedEvent(this));
        } catch (IOException e) {
            LOGGER.error("Error while scanning json files", e);
        }
    }

    @VisibleForTesting
    void doLoadAllFilesIn(Path path) throws IOException {
        Files.walk(path).forEach(this::loadJsonContent);
    }

    private void loadJsonContent(Path path) {
        if (!Files.isRegularFile(path)) {
            LOGGER.debug("path is no file, skipping: {}", path);
            return;
        }
        Optional<BaseFoundryVttObject> systemItemOptional = tryParseSystemItem(path);

        if (systemItemOptional.isPresent()) {
            BaseFoundryVttObject item = systemItemOptional.get();
            String packName = packNameExtractor.getPackName(path, item);
            jsonStorage.addJson(packName, item);
        } else {
            LOGGER.error("Failed to load json at: {}", path.toAbsolutePath());
        }
    }

    private Optional<BaseFoundryVttObject> tryParseSystemItem(Path path) {
        ObjectMapper foundryObjMapperMapper = foundryObjMapper.getMapper();
        JsonFactory mapperFactory = foundryObjMapperMapper.getFactory();
        BaseFoundryVttObject systemItem = null;
        try (JsonParser jsonParser = mapperFactory.createParser(path.toFile())) {
            jsonParser.nextToken();
            while (jsonParser.nextToken() != null) {
                systemItem = foundryObjMapperMapper.readValue(jsonParser, BaseFoundryVttObject.class);
                systemItem.setSrcPath(path);
            }
            if (systemItem.getId() == null) {
                LOGGER.info("Fixed missing id of: {}", path.toAbsolutePath());
                systemItem.setId(idGenerator.generateId());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return Optional.ofNullable(systemItem);
    }
}
