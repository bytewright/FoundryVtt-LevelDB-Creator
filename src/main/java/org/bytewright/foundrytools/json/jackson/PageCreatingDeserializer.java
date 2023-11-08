package org.bytewright.foundrytools.json.jackson;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import org.bytewright.foundrytools.LevelDBUnpacker;
import org.bytewright.foundrytools.config.AppSettings;
import org.bytewright.foundrytools.json.JsonStorage;
import org.bytewright.foundrytools.json.pojo.JournalPage;
import org.bytewright.foundrytools.util.IdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.bytewright.foundrytools.config.AppSettings.PACK_JOURNAL;
import static org.bytewright.foundrytools.json.PackNameExtractor.FOLDER_FLAG;

public class PageCreatingDeserializer extends JsonDeserializer<List<String>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(PageCreatingDeserializer.class);
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
        if (node.getNodeType().equals(JsonNodeType.ARRAY)) {
            List<String> strings = new ArrayList<>(node.size());
            for (int i = 0; i < node.size(); i++) {
                if (node.get(i).getNodeType().equals(JsonNodeType.OBJECT)) {
                    String id = createJournalPage(p, node.get(i));
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

    private String createJournalPage(JsonParser p, JsonNode jsonNode) throws IOException {
        LOGGER.info("migrating journal...", jsonNode);
        ObjectCodec codec = p.getCodec();
        JournalPage page = ((ObjectMapper) codec).readValue(jsonNode.toPrettyString(), JournalPage.class);
        page.setId(idGenerator.generateId());
        Map<String, Object> flagsMap = page.getFlagsMap();
        if (flagsMap == null) {
            page.setFlagsMap(new HashMap<>());
        }
        page.getFlagsMap().put(FOLDER_FLAG, PACK_JOURNAL+".Pages");
        page.setSrcPath(appSettings.getBaseJsonSrcPath().resolve(LevelDBUnpacker.fileNameWithExt(page.getName())));
        jsonStorage.addOrReplace(page);
        return page.getId();
    }
}
