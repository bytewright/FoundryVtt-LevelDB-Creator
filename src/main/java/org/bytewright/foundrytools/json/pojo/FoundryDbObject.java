package org.bytewright.foundrytools.json.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nullable;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

public class FoundryDbObject extends BaseFoundryVttObject {
    protected Integer sort;

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }
}
