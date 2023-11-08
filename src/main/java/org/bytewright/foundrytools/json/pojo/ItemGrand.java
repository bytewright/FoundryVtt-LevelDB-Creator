package org.bytewright.foundrytools.json.pojo;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.LinkedHashMap;
import java.util.Map;

@JsonPropertyOrder({"_id", "type", "level", "title", "configuration"})
public class ItemGrand {
    @JsonProperty("_id")
    private String id;
    private Integer level;
    private String type;
    private String title;
    private Map<String, Object> value;
    private ItemGrantConfiguration configuration;
    private Map<String, Object> other = new LinkedHashMap<String, Object>();

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, Object> getValue() {
        return value;
    }

    public void setValue(Map<String, Object> value) {
        this.value = value;
    }

    public ItemGrantConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(ItemGrantConfiguration configuration) {
        this.configuration = configuration;
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
}
