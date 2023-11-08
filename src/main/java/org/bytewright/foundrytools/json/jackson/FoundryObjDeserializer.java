package org.bytewright.foundrytools.json.jackson;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.bytewright.foundrytools.datageneration.DataGeneratorClassFeatures;
import org.bytewright.foundrytools.json.pojo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Set;

public class FoundryObjDeserializer extends StdDeserializer<BaseFoundryVttObject> {
    private static final Logger LOGGER = LoggerFactory.getLogger(FoundryObjDeserializer.class);
    private static final Set<String> KNOWN_ITEM_TYPES = Set.of("weapon", "equipment", "consumable",
            "tool", "loot", "background", "class", "subclass", "spell", "feat", "backpack");

    protected FoundryObjDeserializer() {
        super(BaseFoundryVttObject.class);
    }

    @Override
    public BaseFoundryVttObject deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        ObjectCodec codec = p.getCodec();
        JsonNode node = codec.readTree(p);
        JsonNode jsonNodeType = node.get("type");

        if (jsonNodeType != null && !jsonNodeType.isNull()) {
            String type = jsonNodeType.asText();
            if (KNOWN_ITEM_TYPES.contains(type)) {
                return codec.treeToValue(node, SystemItem.class);
            } else if ("text".equals(type)) {
                return codec.treeToValue(node, JournalPage.class);
            } else if ("0".equals(type)) {
                return codec.treeToValue(node, RollTableResult.class);
            }
        } else if (node.get("pages") != null && !node.get("pages").isNull()) {
            return codec.treeToValue(node, JournalEntry.class);
        } else if (node.get("changes") != null && !node.get("changes").isNull()) {
            return codec.treeToValue(node, Effect.class);
        } else if (node.get("results") != null && !node.get("results").isNull()) {
            return codec.treeToValue(node, RollTable.class);
        } else if (node.get("drawn") != null && !node.get("drawn").isNull()) {
            return codec.treeToValue(node, RollTableResult.class);
        }
        throw new RuntimeException("Unknown object!" + node);
    }
}
