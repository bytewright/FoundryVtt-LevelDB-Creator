package org.bytewright.foundrytools.leveldb;

import org.bytewright.foundrytools.config.AppSettings;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBIterator;
import org.iq80.leveldb.Options;
import org.iq80.leveldb.WriteBatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.iq80.leveldb.impl.Iq80DBFactory.asString;
import static org.iq80.leveldb.impl.Iq80DBFactory.factory;

public class LevelDBExporter {
    private static final Logger LOGGER = LoggerFactory.getLogger(LevelDBExporter.class);
    private final Map<String, DbHandle> packNameDbCreatorMap = new HashMap<>();
    private final AppSettings appSettings;
    private final LevelDbIdPrefixer idPrefixer;


    public LevelDBExporter(AppSettings appSettings, LevelDbIdPrefixer idPrefixer) {
        this.appSettings = appSettings;
        this.idPrefixer = idPrefixer;
    }

    public boolean initialize(Set<String> packNames) {
        boolean initialized;
        try {
            createExportDirs(packNames);
            initialized = true;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return initialized;
    }

    private void createExportDirs(Set<String> packNames) throws IOException {
        Path exportPath = appSettings.getExportPath();
        if (Files.exists(exportPath)) {
            LOGGER.info("Cleaning out dir: {}", exportPath);
            try {
                Files.walk(exportPath)
                        .filter(Files::isRegularFile)
                        .forEach(this::deleteFile);
                Files.walk(exportPath)
                        .filter(path -> path != exportPath)
                        .forEach(this::deleteFile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        for (String packName : packNames) {
            Path packPath = exportPath.resolve(packName);

            LOGGER.info("Creating lvldb export dir: {}", packPath);
            Files.createDirectories(packPath);
            packNameDbCreatorMap.put(packName, new DbHandle(packName, packPath));
        }
    }

    private void deleteFile(Path path) {
        try {
            Files.delete(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public void add(String packName, SerializedFoundryObj serializedFoundryObj) {
        DbHandle dbHandle = packNameDbCreatorMap.get(packName);
        try {
            String id = idPrefixer.getPrefix(serializedFoundryObj.foundryObject());
            dbHandle.write(id, serializedFoundryObj.json());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void printDb(String packName) {
        DbHandle dbHandle = packNameDbCreatorMap.get(packName);
        try (DB db = dbHandle.get()) {
            try (DBIterator iterator = db.iterator()) {
                for (iterator.seekToFirst(); iterator.hasNext(); iterator.next()) {
                    String key = asString(iterator.peekNext().getKey());
                    String value = asString(iterator.peekNext().getValue());
                    System.out.println(key + " = " + value);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void contentLoadFinished() {
        for (DbHandle dbHandle : packNameDbCreatorMap.values()) {
            try {
                LOGGER.info("Closing db for pack {}", dbHandle.packName);
                dbHandle.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        LOGGER.info("All dbs closed. sending finished event...");
    }

    public void commitPack(String packName) throws IOException {
        DbHandle dbHandle = packNameDbCreatorMap.get(packName);
        dbHandle.commitBatch();
    }

    private static class DbHandle {
        private final String packName;
        private final Path packPath;
        private boolean isOpen = false;
        private DB db = null;
        private WriteBatch writeBatch = null;

        public DbHandle(String packName, Path packPath) {
            this.packName = packName;
            this.packPath = packPath;
        }

        public DB get() throws IOException {
            if (db == null) {
                db = factory.open(packPath.toFile(), dbOptions());
                isOpen = true;

            }
            return db;
        }

        public void close() throws IOException {
            if (isOpen) {
                db.close();
            }
        }

        private Options dbOptions() {
            Options options = new Options();
            options.createIfMissing(true);
            return options;
        }

        public void write(String id, String json) throws IOException {
            getBatch().put(id.getBytes(StandardCharsets.UTF_8), json.getBytes(StandardCharsets.UTF_8));
        }

        private WriteBatch getBatch() throws IOException {
            if (writeBatch == null) {
                DB db = get();
                writeBatch = db.createWriteBatch();
            }
            return writeBatch;
        }

        public void commitBatch() throws IOException {
            if (isOpen) {
                DB db = get();
                db.write(writeBatch);
            }
            writeBatch = null;
        }
    }
}
