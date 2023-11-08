package org.bytewright.foundrytools.json.pojo;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@JsonPropertyOrder({"description", "source", "classIdentifier", "identifier", "advancement", "type"})
public class SystemInformation {
    private Description description;
    private String source;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String classIdentifier;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String identifier;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Map<String, String> type;
    private List<Object> advancement;
    private Map<String, Object> other = new LinkedHashMap<String, Object>();

    public String getClassIdentifier() {
        return classIdentifier;
    }

    public void setClassIdentifier(String classIdentifier) {
        this.classIdentifier = classIdentifier;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public Map<String, String> getType() {
        return type;
    }

    public void setType(Map<String, String> type) {
        this.type = type;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public List<Object> getAdvancement() {
        return advancement;
    }

    public void setAdvancement(List<Object> advancement) {
        this.advancement = advancement;
    }

    public Description getDescription() {
        return description;
    }

    public void setDescription(Description description) {
        this.description = description;
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
        return "SystemInformation{" +
                "source='" + source + '\'' +
                ", description=" + description +
                '}';
    }
}
