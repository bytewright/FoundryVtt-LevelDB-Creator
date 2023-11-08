package org.bytewright.foundrytools.leveldb;

import org.bytewright.foundrytools.json.JsonStorage;
import org.bytewright.foundrytools.json.pojo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class LevelDbIdPrefixer {
    @Autowired
    private JsonStorage jsonStorage;

    public String getPrefix(BaseFoundryVttObject object) {
        return prefix(object) + object.getId();
    }

    private String prefix(BaseFoundryVttObject foundryDbObject) {
        return switch (foundryDbObject) {
            case SystemItem ignored -> "!items!";
            case Effect ignored -> effect(ignored);
            case CompendiumFolderItem ignored -> "!folders!";
            case JournalEntry ignored -> "!journal!";
            case JournalPage ignored -> journalpage(ignored);
            case RollTable ignored -> "!tables!";
            case RollTableResult ignored -> rolltableResult(ignored);
            default -> throw new IllegalStateException("Tried to add unknown type to lvldb! " + foundryDbObject);
        };
    }

    private String effect(Effect effect) {
        Optional<String> parentId = jsonStorage.streamAll(SystemItem.class)
                .filter(t -> t.getEffects() != null)
                .filter(t -> t.getEffects().contains(effect.getId()))
                .findAny()
                .map(BaseFoundryVttObject::getId);
        if (parentId.isEmpty()) {
            parentId = Optional.of("NO_PARENT");
            //throw new RuntimeException("Failed to find parent of: " + effect);
        }
        return "!items.effects!" + parentId.get() + ".";
    }

    private String journalpage(JournalPage journalPage) {
        String parentId = jsonStorage.streamAll(JournalEntry.class)
                .filter(t -> t.getPages().contains(journalPage.getId()))
                .findAny()
                .map(BaseFoundryVttObject::getId)
                .orElseThrow();
        return "!journal.pages!" + parentId + ".";
    }

    private String rolltableResult(RollTableResult rollTableResult) {
        String parentId = jsonStorage.streamAll(RollTable.class)
                .filter(t -> t.getRollTableResults().contains(rollTableResult.getId()))
                .findAny()
                .map(BaseFoundryVttObject::getId)
                .orElseThrow();
        return "!tables.results!" + parentId + ".";
    }
}
