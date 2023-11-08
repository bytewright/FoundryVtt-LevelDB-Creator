package org.bytewright.foundrytools.json.pojo;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

import java.util.LinkedHashMap;
import java.util.Map;

public class CompendiumFolderItem extends FoundryDbObject {
    private String sorting;
    private Map<String, Object> other;

    public CompendiumFolderItem() {
        this.sort = 100_000;
        this.sorting = "a";
        this.other = new LinkedHashMap<>();
    }

    public String getSorting() {
        return sorting;
    }

    public void setSorting(String sorting) {
        this.sorting = sorting;
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
        return "CompendiumFolderItem{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", parentDir='" + folder + '\'' +
                '}';
    }
}
