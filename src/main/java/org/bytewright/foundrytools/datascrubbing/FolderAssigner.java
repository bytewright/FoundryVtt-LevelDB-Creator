package org.bytewright.foundrytools.datascrubbing;

import org.bytewright.foundrytools.config.AppSettings;
import org.bytewright.foundrytools.json.PackNameExtractor;
import org.bytewright.foundrytools.json.pojo.SystemInformation;
import org.bytewright.foundrytools.json.pojo.SystemItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.bytewright.foundrytools.config.AppSettings.PACK_ITEMS;

@Service
public class FolderAssigner extends DataScrubber {
    private static final Set<String> ARMOR_TYPES = Set.of("light", "heavy", "medium");
    @Autowired
    private AppSettings appSettings;
    @Autowired
    private PackNameExtractor packNameExtractor;

    @Override
    public int getOrder() {
        return ORDER_DEFAULT;
    }

    @Override
    public List<Report> run() {
        List<SystemItem> systemItems = jsonStorage.streamAll(SystemItem.class)
                .filter(systemItem -> "equipment".equals(systemItem.getType()))
                .toList();
        Set<String> seenArmorTypes = new HashSet<>();
        for (SystemItem systemItem : systemItems) {
            SystemInformation systemInformation = systemItem.getSystemInformation();
            Map<String, Object> armorInfo = (Map<String, Object>) systemInformation.any().get("armor");
            if (armorInfo != null) {
                String armorType = (String) armorInfo.get("type");
                seenArmorTypes.add(armorType);
                if ("trinket".equals(armorType)) {
                    moveToSubdir(systemItem, "equipment/", PACK_ITEMS + ".equipment");
                }
                if ("shield".equals(armorType)) {
                    moveToSubdir(systemItem, "equipment/shields/", PACK_ITEMS + ".shields");
                }
                if ("clothing".equals(armorType)) {
                    moveToSubdir(systemItem, "equipment/clothing/", PACK_ITEMS + ".clothing");
                }
                if ("vehicle".equals(armorType)) {
                    moveToSubdir(systemItem, "equipment/vehicles/", PACK_ITEMS + ".vehicles");
                }
                if (ARMOR_TYPES.contains(armorType)) {
                    moveToSubdir(systemItem, "equipment/armor/", PACK_ITEMS + ".armor");
                }
            }
        }

        return List.of();
    }

    private void moveToSubdir(SystemItem systemItem, String dirPath, String compendiumFolder) {
        Path subDir = appSettings.getBaseJsonSrcPath().resolve(dirPath);
        try {
            Files.createDirectories(subDir);
            String fileName = systemItem.getSrcPath().toFile().getName();
            Path newPath = subDir.resolve(fileName);
            Files.move(systemItem.getSrcPath(), newPath);
            systemItem.setSrcPath(newPath);
            packNameExtractor.updatePack(systemItem, compendiumFolder);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
