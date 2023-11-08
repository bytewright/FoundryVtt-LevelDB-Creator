package org.bytewright.foundrytools.json.pojo;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class RollTable extends BaseFoundryVttObject {
    String formula;
    Boolean replacement;
    Boolean displayRoll;
    private Map<String, Object> otherEntries = new LinkedHashMap<>();

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public Boolean getReplacement() {
        return replacement;
    }

    public void setReplacement(Boolean replacement) {
        this.replacement = replacement;
    }

    public Boolean getDisplayRoll() {
        return displayRoll;
    }

    public void setDisplayRoll(Boolean displayRoll) {
        this.displayRoll = displayRoll;
    }

    @JsonAnyGetter
    public Map<String, Object> getOtherEntries() {
        return otherEntries;
    }

    // "any setter" needed for deserialization
    @JsonAnySetter
    public void setOther(String name, Object value) {
        otherEntries.put(name, value);
    }
}
