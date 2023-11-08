package org.bytewright.foundrytools.json.pojo;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

import java.util.LinkedHashMap;
import java.util.Map;

public class JournalPage extends BaseFoundryVttObject {
    Map<String, Object> title;
    JournalPageText text;
    private Map<String, Object> otherEntries = new LinkedHashMap<>();

    public Map<String, Object> getTitle() {
        return title;
    }

    public void setTitle(Map<String, Object> title) {
        this.title = title;
    }

    public JournalPageText getText() {
        return text;
    }

    public void setText(JournalPageText text) {
        this.text = text;
    }

    @JsonAnyGetter
    public Map<String, Object> getOtherEntries() {
        return otherEntries;
    }

    @JsonAnySetter
    public void setOther(String name, Object value) {
        otherEntries.put(name, value);
    }
}
