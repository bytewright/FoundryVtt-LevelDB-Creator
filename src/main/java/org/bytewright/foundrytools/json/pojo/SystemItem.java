package org.bytewright.foundrytools.json.pojo;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.bytewright.foundrytools.json.jackson.EffectCreatingDeserializer;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@JsonPropertyOrder({"_id", "name", "type", "img", "system"})
public class SystemItem extends FoundryDbObject {

    private String img;
    @JsonDeserialize(using = EffectCreatingDeserializer.class)
    protected List<String> effects;
    @JsonProperty("system")
    @JsonAlias({"system", "data"})
    @JsonInclude(value = JsonInclude.Include.NON_ABSENT, content = JsonInclude.Include.NON_NULL)
    private SystemInformation systemInformation;
    private Map<String, Object> other = new LinkedHashMap<String, Object>();

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public List<String> getEffects() {
        return effects;
    }

    public void setEffects(List<String> effects) {
        this.effects = effects;
    }

    public SystemInformation getSystemInformation() {
        return systemInformation;
    }

    public void setSystemInformation(SystemInformation systemInformation) {
        this.systemInformation = systemInformation;
    }

    @JsonAnyGetter
    public Map<String, Object> any() {
        return other;
    }

    // "any setter" needed for deserialization
    @JsonAnySetter
    public void set(String name, Object value) {
        other.put(name, value);
    }

    @Override
    public String toString() {
        return "SystemItem{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", systemInformation=" + systemInformation +
                '}';
    }
}
