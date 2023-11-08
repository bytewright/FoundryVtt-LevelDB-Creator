package org.bytewright.foundrytools.json;

import org.bytewright.foundrytools.json.pojo.BaseFoundryVttObject;
import org.bytewright.foundrytools.json.pojo.CompendiumFolderItem;
import org.bytewright.foundrytools.json.pojo.FoundryDbObject;
import org.bytewright.foundrytools.json.pojo.SystemItem;
import org.bytewright.foundrytools.util.IdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Stream;

public class FoundryPack {
    private static final Logger LOGGER = LoggerFactory.getLogger(FoundryPack.class);
    private final String packName;
    private final JsonStorage jsonStorage;
    private final IdGenerator idGenerator;
    private final List<BaseFoundryVttObject> rootContent = new LinkedList<>();
    private final Map<String, CompendiumFolderItem> folderNameIndex = new HashMap<>();
    private final Map<CompendiumFolderItem, List<BaseFoundryVttObject>> folderItems = new HashMap<>();

    public FoundryPack(String packName, JsonStorage jsonStorage, IdGenerator idGenerator) {
        this.packName = packName;
        this.jsonStorage = jsonStorage;
        this.idGenerator = idGenerator;
    }

    public void addToRoot(BaseFoundryVttObject jsonContent) {
        rootContent.add(jsonContent);
    }

    public void addToFolder(BaseFoundryVttObject jsonContent, String... folderNames) {
        String prevFolderId = null;
        for (String folderName : folderNames) {
            if (folderName == null)
                throw new IllegalArgumentException("Null dir for content: " + jsonContent);
            if (!folderNameIndex.containsKey(folderName)) {
                CompendiumFolderItem folderItem = newFolder(prevFolderId, folderName);
                folderNameIndex.put(folderName, folderItem);
            }
            CompendiumFolderItem folderItem = folderNameIndex.get(folderName);
            jsonContent.setCompendiumfolder(folderItem.getId());
            List<BaseFoundryVttObject> objects = folderItems.computeIfAbsent(folderItem, ignore -> new LinkedList<>());
            Optional<BaseFoundryVttObject> duplicate = objects.stream()
                    .filter(foundryDbObject -> foundryDbObject.getId().equals(jsonContent.getId()))
                    .findAny();
            if (duplicate.isPresent()) {
                LOGGER.debug("Replacing object with id: {}", jsonContent.getId());
                objects.remove(duplicate.get());
            }
            objects.add(jsonContent);
            prevFolderId = folderItem.getId();
        }
    }

    private CompendiumFolderItem newFolder(String parentFolderId, String name) {
        LOGGER.info("Creating folder {} with parent {} in pack {}", name, parentFolderId, packName);
        CompendiumFolderItem folderItem = new CompendiumFolderItem();
        folderItem.setId(idGenerator.generateId(name));
        folderItem.setName(name);
        folderItem.setType("Item");
        folderItem.setFolder(parentFolderId);
        jsonStorage.addFolderItemToIndex(folderItem);
        return folderItem;
    }

    public Stream<CompendiumFolderItem> exportFolders() {
        return folderItems.keySet().stream();
    }

    public Stream<BaseFoundryVttObject> exportContent() {
        return Stream.concat(rootContent.stream(), folderItems.values().stream().flatMap(Collection::stream))
                .map(this::setFolderToExportValue);
    }

    private BaseFoundryVttObject setFolderToExportValue(BaseFoundryVttObject obj) {
        obj.setFolder(obj.getCompendiumfolder());
        return obj;
    }
}
