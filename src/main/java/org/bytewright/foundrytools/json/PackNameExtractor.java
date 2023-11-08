package org.bytewright.foundrytools.json;

import org.bytewright.foundrytools.json.pojo.BaseFoundryVttObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

@Service
public class PackNameExtractor {

    public static final String FOLDER_FLAG = "LvlDbCompendiumFolder";
    private static final Logger LOGGER = LoggerFactory.getLogger(PackNameExtractor.class);

    public String getPackName(Path path, BaseFoundryVttObject item) {
        return getFromFlags(item).orElse(path.getParent().toFile().getName());
    }

    private Optional<String> getFromFlags(BaseFoundryVttObject item) {
        Map<String, Object> flagsMap = item.getFlagsMap();
        if (flagsMap.containsKey(FOLDER_FLAG)) {
            return Optional.of((String) flagsMap.get(FOLDER_FLAG));
        }
        return Optional.empty();
    }

    public String getFromFlagsOrThrow(BaseFoundryVttObject item) {
        return getFromFlags(item).orElseThrow(() -> new RuntimeException("Item has no folder flags: " + item));
    }

    public void updatePack(BaseFoundryVttObject item, String newPackFolder) {
        String oldFolder = getFromFlagsOrThrow(item);
        item.getFlagsMap().put(FOLDER_FLAG, newPackFolder);
        LOGGER.debug("{}: Updated folder from {} to {}", item.getId(), oldFolder, newPackFolder);
    }
}
