package org.bytewright.foundrytools.json.jackson;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.bytewright.foundrytools.json.pojo.SystemInformation;
import org.bytewright.foundrytools.json.pojo.SystemItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;

public class SystemItemDeserializer extends StdDeserializer<SystemItem> {
    private static final Logger LOGGER = LoggerFactory.getLogger(SystemItemDeserializer.class);
    private static final Set<String> KNOWN_ITEM_TYPES = Set.of("weapon", "equipment", "consumable",
            "tool", "loot", "background", "class", "subclass", "spell", "feat", "backpack");

    protected SystemItemDeserializer() {
        super(SystemItem.class);
    }

    @Override
    public SystemItem deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        ObjectCodec codec = p.getCodec();
        JsonNode node = codec.readTree(p);
        SystemItem result = new SystemItem();
        result.setId(node.get("_id").asText());
        result.setImg(node.get("img").asText());
        result.setType(node.get("type").asText());
        result.setName(node.get("name").asText());
        JsonNode jsonNodeType = Optional.ofNullable(node.get("system")).orElseGet(() -> node.get("data"));
        if (jsonNodeType != null && !jsonNodeType.isNull()) {
            result.setSystemInformation(codec.treeToValue(node, SystemInformation.class));
        }
        return result;
    }
}
