package org.bytewright.foundrytools.json.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.annotation.Nullable;
import org.bytewright.foundrytools.json.jackson.PageCreatingDeserializer;
import org.bytewright.foundrytools.json.jackson.TableResultsCreatingDeserializer;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@JsonPropertyOrder({"_id", "name", "type"})
public class BaseFoundryVttObject {
    @JsonProperty("_id")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    protected String id;
    protected String name;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    protected String type;

    @JsonInclude(value = JsonInclude.Include.NON_EMPTY, content = JsonInclude.Include.NON_NULL)
    @JsonDeserialize(using = PageCreatingDeserializer.class)
    protected List<String> pages;
    @JsonInclude(value = JsonInclude.Include.NON_EMPTY, content = JsonInclude.Include.NON_NULL)
    @JsonDeserialize(using = TableResultsCreatingDeserializer.class)
    @JsonProperty("results")
    protected List<String> rollTableResults;
    @JsonIgnore
    protected Path srcPath;
    @JsonIgnore
    protected String compendiumfolder;
    @JsonProperty("flags")
    protected Map<String, Object> flagsMap = new LinkedHashMap<>();
    @JsonProperty("_stats")
    @JsonInclude(value = JsonInclude.Include.NON_EMPTY, content = JsonInclude.Include.NON_NULL)
    protected Map<String, Object> statsMap = new LinkedHashMap<>();
    @JsonInclude(JsonInclude.Include.NON_NULL)
    protected Integer sort;
    protected String folder;

    public List<String> getRollTableResults() {
        return rollTableResults;
    }

    public void setRollTableResults(List<String> rollTableResults) {
        this.rollTableResults = rollTableResults;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Path getSrcPath() {
        return srcPath;
    }

    public void setSrcPath(Path srcPath) {
        this.srcPath = srcPath;
    }

    public String getCompendiumfolder() {
        return compendiumfolder;
    }

    public void setCompendiumfolder(String compendiumfolder) {
        this.compendiumfolder = compendiumfolder;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Nullable
    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    @Nullable
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Nullable
    public List<String> getPages() {
        return pages;
    }

    public void setPages(List<String> pages) {
        this.pages = pages;
    }

    public Map<String, Object> getFlagsMap() {
        return flagsMap;
    }

    public void setFlagsMap(Map<String, Object> flagsMap) {
        this.flagsMap = flagsMap;
    }

    public Map<String, Object> getStatsMap() {
        return statsMap;
    }

    public void setStatsMap(Map<String, Object> statsMap) {
        this.statsMap = statsMap;
    }
}
