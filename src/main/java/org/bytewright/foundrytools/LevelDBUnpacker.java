package org.bytewright.foundrytools;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBIterator;
import org.iq80.leveldb.Options;
import org.iq80.leveldb.impl.Filename;
import org.iq80.leveldb.impl.Iq80DBFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;

public class LevelDBUnpacker {

    public static void main(String[] args) throws IOException {
        Path pathInput = getBaseProjectPath().resolve("unpack");
        Path pathOutput = getBaseProjectPath().resolve("unpacked");

        unpackDbFiles(pathInput, pathOutput);
        unpackLevelDbs(pathInput, pathOutput);
    }

    private static void unpackLevelDbs(Path pathInput, Path pathOutput) {
        try {
            Set<Path> lvlDbDirs = Files.walk(pathInput)
                    .filter(path -> path.toFile().getName().equals(Filename.currentFileName()))
                    .map(Path::getParent)
                    .collect(Collectors.toSet());
            for (Path lvlDbDir : lvlDbDirs) {
                System.out.printf("Found leveldb at dir %s", lvlDbDir.toAbsolutePath());
                unpackLevelDb(lvlDbDir, pathOutput.resolve(lvlDbDir.toFile().getName()));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private static void unpackDbFiles(Path pathInput, Path pathOutput) {
        try {
            Files.walk(pathInput)
                    .filter(path -> path.toFile().getName().endsWith(".db"))
                    .forEach(path -> unpackDb(path, pathOutput.resolve(path.toFile().getName().replace(".db", ""))));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void unpackLevelDb(Path lvlDbDir, Path outDir) throws IOException {
        Files.walk(lvlDbDir)
                .filter(path -> path.toFile().getName().endsWith("ldb"))
                .forEach(LevelDBUnpacker::renameToSst);
        Options options = new Options();
        options.createIfMissing(false);
        Map<String, String> content = new LinkedHashMap<>();
        try (DB db = Iq80DBFactory.factory.open(lvlDbDir.toFile(), options)) {
            try (DBIterator iterator = db.iterator()) {
                for (iterator.seekToFirst(); iterator.hasNext(); iterator.next()) {
                    String key = Iq80DBFactory.asString(iterator.peekNext().getKey());
                    String value = Iq80DBFactory.asString(iterator.peekNext().getValue());
                    content.put(key, value);
                }
            } catch (IOException e) {
                System.out.println("Exception while iterating DB! " + e.getMessage());
                e.printStackTrace();
            }
        } catch (IOException e) {
            System.out.println("Exception while opening DB! " + e.getMessage());
            e.printStackTrace();
        }

        System.out.printf("Fetched %d entries from LevelDB%n", content.size());
        for (Map.Entry<String, String> entry : content.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            System.out.println(key + " = " + value);
            writeJson(outDir, value);
        }
    }

    private static void renameToSst(Path path) {
        String fileName = path.toFile().getName();
        String newFileName = fileName.replace(".ldb", ".sst");
        try {
            Files.move(path, path.getParent().resolve(newFileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void unpackDb(Path path, Path pathOutput) {
        try {
            List<String> lines = Files.readAllLines(path);
            for (String line : lines) {
                writeJson(pathOutput, line);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void writeJson(Path pathOutput, String line) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        LinkedHashMap value = objectMapper.readValue(line, LinkedHashMap.class);
        String fileNameRaw = (String) Optional.ofNullable(value.get("name")).orElseGet(() -> value.get("_id"));
        String fileName = fileName(fileNameRaw)+".json";
        String prettyJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(value);
        Files.createDirectories(pathOutput);
        Path resolve = pathOutput.resolve(fileName);
        if (Files.exists(resolve)) {
            String fileName1 = fileName + "_" + fileName((String) value.get("_id"));
            resolve = pathOutput.resolve(fileNameWithExt(fileName1));
            System.out.println("Name conflict, writing as: " + resolve.toAbsolutePath());
        }
        Files.writeString(resolve, prettyJson, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    public static Path getBaseProjectPath() {
        Path basePath = new File("").toPath().toAbsolutePath();
        return basePath;
    }

    public static String fileName(String name) {
        return name.replace(" ", "_")
                .replace(":", "")
                .replace("/", "_")
                .replace("\\", "_")
                .replace("?", "")
                .strip();
    }

    public static String fileNameWithExt(String name) {
        return fileName(name) + ".json";
    }
}
