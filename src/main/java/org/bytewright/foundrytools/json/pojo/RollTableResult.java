package org.bytewright.foundrytools.json.pojo;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.LinkedHashMap;
import java.util.Map;

public class RollTableResult extends BaseFoundryVttObject {
    Integer weight;
    String text;
    String img;
    @JsonProperty("type")
    Integer typeInt;
    private Map<String, Object> otherEntries = new LinkedHashMap<>();

    public Integer getTypeInt() {
        return typeInt;
    }

    public void setTypeInt(Integer typeInt) {
        this.typeInt = typeInt;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
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
