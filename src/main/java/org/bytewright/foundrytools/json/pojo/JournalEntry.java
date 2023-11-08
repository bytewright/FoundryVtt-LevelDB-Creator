package org.bytewright.foundrytools.json.pojo;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

import java.util.LinkedHashMap;
import java.util.Map;

public class JournalEntry extends BaseFoundryVttObject {

    private Map<String, Object> otherEntries = new LinkedHashMap<>();

    @JsonAnyGetter
    public Map<String, Object> getOtherEntries() {
        return otherEntries;
    }

    @JsonAnySetter
    public void setOther(String name, Object value) {
        otherEntries.put(name, value);
    }
}
