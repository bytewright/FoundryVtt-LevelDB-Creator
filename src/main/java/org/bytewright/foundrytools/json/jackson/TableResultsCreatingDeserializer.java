package org.bytewright.foundrytools.json.jackson;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import org.bytewright.foundrytools.LevelDBUnpacker;
import org.bytewright.foundrytools.config.AppSettings;
import org.bytewright.foundrytools.json.JsonStorage;
import org.bytewright.foundrytools.json.pojo.RollTableResult;
import org.bytewright.foundrytools.util.IdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.bytewright.foundrytools.config.AppSettings.PACK_ROLLTABLE;
import static org.bytewright.foundrytools.json.PackNameExtractor.FOLDER_FLAG;

public class TableResultsCreatingDeserializer extends JsonDeserializer<List<String>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(TableResultsCreatingDeserializer.class);
    @Autowired
    private JsonStorage jsonStorage;
    @Autowired
    private IdGenerator idGenerator;
    @Autowired
    private AppSettings appSettings;

    @Override
    public List<String> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        ObjectCodec oc = p.getCodec();
        JsonNode node = oc.readTree(p);
        String[] fileNames = idGenerator.generatePrefixedRndFilenames(node.size());
        if (node.getNodeType().equals(JsonNodeType.ARRAY)) {
            List<String> strings = new ArrayList<>(node.size());
            for (int i = 0; i < node.size(); i++) {

                if (node.get(i).getNodeType().equals(JsonNodeType.OBJECT)) {
                    String id = createRollTableResult(fileNames[i], p, node.get(i));
                    strings.add(id);
                } else if (node.get(i).getNodeType().equals(JsonNodeType.STRING)) {
                    strings.add(node.get(i).asText());
                } else {
                    throw new IllegalArgumentException("No idea whats in this journals pages: " + p);
                }
            }
            return strings;
        }
        return List.of();
    }

    private String createRollTableResult(String fileName, JsonParser p, JsonNode jsonNode) throws JsonProcessingException {
        LOGGER.info("migrating rolltable...", jsonNode);
        ObjectCodec codec = p.getCodec();
        RollTableResult result = ((ObjectMapper) codec).readValue(jsonNode.toPrettyString(), RollTableResult.class);
        result.setId(idGenerator.generateId());
        Map<String, Object> flagsMap = result.getFlagsMap();
        if (flagsMap == null) {
            result.setFlagsMap(new HashMap<>());
        }
        result.getFlagsMap().put(FOLDER_FLAG, PACK_ROLLTABLE+".Results");
        String fileNameWithExt = LevelDBUnpacker.fileNameWithExt(fileName);
        result.setSrcPath(appSettings.getBaseJsonSrcPath().resolve(fileNameWithExt));
        jsonStorage.addOrReplace(result);
        return result.getId();
    }
}
