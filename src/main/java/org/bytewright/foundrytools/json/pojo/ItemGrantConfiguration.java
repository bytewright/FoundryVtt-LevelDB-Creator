package org.bytewright.foundrytools.json.pojo;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ItemGrantConfiguration {
    private List<String> items;
    private Map<String, Object> other = new LinkedHashMap<String, Object>();

    public List<String> getItems() {
        return items;
    }

    public void setItems(List<String> items) {
        this.items = items;
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
