package org.bytewright.foundrytools.json.pojo;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

import java.util.LinkedHashMap;
import java.util.Map;

public class Description {
    private String value;
    private Map<String, Object> other = new LinkedHashMap<String, Object>();

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
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
        return "Description{len =" + value.length() + "}";
    }
}
