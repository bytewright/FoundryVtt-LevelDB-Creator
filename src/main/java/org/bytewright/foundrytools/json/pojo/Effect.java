package org.bytewright.foundrytools.json.pojo;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Effect extends BaseFoundryVttObject {
    String icon;
    String origin;
    Boolean transfer;
    Boolean disabled;
    List<EffectChange> changes;
    String description;
    Map<String, Object> duration;
    private Map<String, Object> otherEntries = new LinkedHashMap<>();

    @JsonAnyGetter
    public Map<String, Object> getOtherEntries() {
        return otherEntries;
    }

    @JsonAnySetter
    public void setOther(String name, Object value) {
        otherEntries.put(name, value);
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public Boolean getTransfer() {
        return transfer;
    }

    public void setTransfer(Boolean transfer) {
        this.transfer = transfer;
    }

    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    public List<EffectChange> getChanges() {
        return changes;
    }

    public void setChanges(List<EffectChange> changes) {
        this.changes = changes;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, Object> getDuration() {
        return duration;
    }

    public void setDuration(Map<String, Object> duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "Effect{" +
                "icon='" + icon + '\'' +
                ", origin='" + origin + '\'' +
                ", transfer=" + transfer +
                ", disabled=" + disabled +
                ", changes=" + changes +
                ", description='" + description + '\'' +
                ", duration=" + duration +
                ", otherEntries=" + otherEntries +
                '}';
    }
}
