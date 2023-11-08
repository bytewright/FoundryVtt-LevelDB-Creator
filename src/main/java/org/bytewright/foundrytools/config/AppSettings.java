package org.bytewright.foundrytools.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Path;

import static org.bytewright.foundrytools.util.IdGenerator.GLOBAL_ID_PREFIX;

@Service
public class AppSettings {
    public static final String COMP_NAME = GLOBAL_ID_PREFIX + "-archive";
    public static final String PACK_SPELLS = GLOBAL_ID_PREFIX + "-spells";
    public static final String PACK_JOURNAL = GLOBAL_ID_PREFIX + "-journalentries";
    public static final String PACK_ROLLTABLE = GLOBAL_ID_PREFIX + "-rolltables";
    public static final String PACK_ITEMS = GLOBAL_ID_PREFIX + "-items";
    public static final String PACK_CHARCREATION = GLOBAL_ID_PREFIX + "-charcreation";
    private static final Logger LOGGER = LoggerFactory.getLogger(AppSettings.class);
    @Autowired
    private ApplicationArguments applicationArguments;

    public Path getBaseProjectPath() {
        Path basePath = new File("").toPath().toAbsolutePath();
        return basePath;
    }

    public Path getBaseJsonSrcPath() {
        Path basePath = getBaseProjectPath();
        LOGGER.debug("Base path is: {}", basePath);
        return basePath.resolve("packs/src/");
    }

    public Path getExportPath() {
        Path basePath = getBaseProjectPath();
        return basePath.resolve("packs/lvldb/");
    }

    public Path getClassFeatureExportPath() {
        Path basePath = getBaseProjectPath();
        return basePath.resolve("packs/src/features-class/");
    }

    public Path getImportJsonPath() {
        Path basePath = getBaseProjectPath();
        return basePath.resolve("packs/import/");
    }
}
