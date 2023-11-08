package org.bytewright.foundrytools.json.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bytewright.foundrytools.json.pojo.BaseFoundryVttObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Component;

@Component
public class FoundryObjMapper {
    private final ObjectMapper mapper;

    @Autowired
    public FoundryObjMapper(ApplicationContext appContext) {
        this.mapper = Jackson2ObjectMapperBuilder.json()
                .applicationContext(appContext)
                .deserializerByType(BaseFoundryVttObject.class, new FoundryObjDeserializer())
                .featuresToEnable(JsonParser.Feature.AUTO_CLOSE_SOURCE)
                .build();
    }

    public ObjectMapper getMapper() {
        return mapper;
    }

    public BaseFoundryVttObject deserializeAndCopyTransient(String json, BaseFoundryVttObject object) throws JsonProcessingException {
        BaseFoundryVttObject baseFoundryVttObject = mapper.readValue(json, BaseFoundryVttObject.class);
        baseFoundryVttObject.setSrcPath(object.getSrcPath());
        baseFoundryVttObject.setCompendiumfolder(object.getCompendiumfolder());
        return baseFoundryVttObject;
    }
}
