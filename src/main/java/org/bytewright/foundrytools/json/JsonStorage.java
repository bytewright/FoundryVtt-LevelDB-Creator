package org.bytewright.foundrytools.json;

import org.bytewright.foundrytools.json.pojo.BaseFoundryVttObject;
import org.bytewright.foundrytools.json.pojo.CompendiumFolderItem;
import org.bytewright.foundrytools.util.IdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Stream;

@Service
public class JsonStorage {
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonStorage.class);
    private final Map<String, FoundryPack> jsonPacks = new LinkedHashMap<>();
    private final Map<String, BaseFoundryVttObject> objectIndex = new HashMap<>();
    @Autowired
    private IdGenerator idGenerator;
    @Autowired
    private PackNameExtractor packNameExtractor;

    public void addJson(String packDir, BaseFoundryVttObject jsonContent) {
        if (packDir == null || packDir.isEmpty()) {
            throw new IllegalArgumentException("packdir is illegal: " + packDir);
        }
        if (objectIndex.containsKey(jsonContent.getId())) {
            LOGGER.debug("Index already contains item with id {}, overriding it wiht: {}", jsonContent.getId(), jsonContent);
        }
        LOGGER.debug("Adding to pack '{}': {}", packDir, jsonContent.getId());
        String[] split = packDir.split("\\.");
        FoundryPack foundryPack = jsonPacks.computeIfAbsent(split[0], this::newFoundryPack);
        if (split.length == 1) {
            if (!split[0].startsWith(IdGenerator.GLOBAL_ID_PREFIX)) {
                LOGGER.info("Adding {} to a non-prefixed pack: {}", jsonContent.getId(), split[0]);
            }
            foundryPack.addToRoot(jsonContent);
        } else {
            foundryPack.addToFolder(jsonContent, Arrays.copyOfRange(split, 1, split.length));
        }
        objectIndex.put(jsonContent.getId(), jsonContent);
        int totalSize = totalSize();
        if (totalSize % 1000 == 0) {
            LOGGER.info("Storage contains {} json files", totalSize);
        }
    }

    private FoundryPack newFoundryPack(String id) {
        return new FoundryPack(id, this, idGenerator);
    }

    public int totalSize() {
        return objectIndex.size();
    }

    public Set<String> getPackNames() {
        return jsonPacks.keySet();
    }

    public Stream<BaseFoundryVttObject> streamAllItems() {
        return objectIndex.values().stream();
    }

    public <T extends BaseFoundryVttObject> Stream<T> streamAll(Class<T> aClass) {
        return streamAllItems()
                .filter(aClass::isInstance)
                .map(aClass::cast);
    }

    void addFolderItemToIndex(CompendiumFolderItem folderItem) {
        objectIndex.put(folderItem.getId(), folderItem);
    }

    public FoundryPack getPack(String packName) {
        return jsonPacks.get(packName);
    }

    public void addOrReplace(BaseFoundryVttObject systemItem) {
        String packDir = packNameExtractor.getFromFlagsOrThrow(systemItem);
        addJson(packDir, systemItem);
    }

    public Stream<BaseFoundryVttObject> streamAllOfFoundryType(String type) {
        return streamAllItems()
                .filter(foundryDbObject -> type.equals(foundryDbObject.getType()));
    }

    public BaseFoundryVttObject getById(String unusedId) {
        return objectIndex.get(unusedId);
    }

    public void updateId(BaseFoundryVttObject object) {
        Map.Entry<String, BaseFoundryVttObject> objectEntry = objectIndex.entrySet()
                .stream()
                .filter(entry -> entry.getValue() == object)
                .findFirst()
                .orElseThrow();
        if (!object.getId().equals(objectEntry.getKey())) {
            objectIndex.put(object.getId(), object);
            objectIndex.remove(objectEntry.getKey());
        }
    }
}
