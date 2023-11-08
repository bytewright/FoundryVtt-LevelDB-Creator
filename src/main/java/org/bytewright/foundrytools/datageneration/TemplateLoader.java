package org.bytewright.foundrytools.datageneration;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bytewright.foundrytools.config.AppSettings;
import org.bytewright.foundrytools.datageneration.pojo.AdvTableTemplate;
import org.bytewright.foundrytools.json.pojo.ItemGrand;
import org.bytewright.foundrytools.json.pojo.SystemItem;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class TemplateLoader implements InitializingBean {
    private static final String TEMPLATE_FEATURE_PATH = "packs/templates/_classfeature.json";
    private static final String TEMPLATE_ADV_TABLE_PATH = "packs/templates/_advancement_table.json";
    private static final String TEMPLATE_ITEMGRANT_TABLE_PATH = "packs/templates/_Advancement_ItemGrand.json";
    private final Map<String, String> templateMap = new LinkedHashMap<>();
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private AppSettings appSettings;

    @Override
    public void afterPropertiesSet() throws Exception {
        Path baseProjectPath = appSettings.getBaseProjectPath();
        templateMap.put("feature", loadJsonString(baseProjectPath.resolve(TEMPLATE_FEATURE_PATH)));
        templateMap.put("advTable", loadJsonString(baseProjectPath.resolve(TEMPLATE_ADV_TABLE_PATH)));
        templateMap.put("itemgrant", loadJsonString(baseProjectPath.resolve(TEMPLATE_ITEMGRANT_TABLE_PATH)));
    }

    public AdvTableTemplate getAdvTableTemplate() {
        try {
            return mapper.readValue(templateMap.get("advTable"), AdvTableTemplate.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public ItemGrand getItemGrantTemplate() {
        try {
            return mapper.readValue(templateMap.get("itemgrant"), ItemGrand.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public SystemItem getFeatureTemplate() {
        try {
            return mapper.readValue(templateMap.get("feature"), SystemItem.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private String loadJsonString(Path path) {
        try {
            return Files.readString(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
