package org.bytewright.foundrytools.datascrubbing;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bytewright.foundrytools.config.AppSettings;
import org.bytewright.foundrytools.json.jackson.FoundryObjMapper;
import org.bytewright.foundrytools.json.pojo.BaseFoundryVttObject;
import org.bytewright.foundrytools.json.pojo.Effect;
import org.bytewright.foundrytools.json.pojo.SystemInformation;
import org.bytewright.foundrytools.json.pojo.SystemItem;
import org.bytewright.foundrytools.util.IdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class IdMatcher extends DataScrubber {
    public static final Pattern pattern = Pattern.compile(IdGenerator.ID_PATTERN_REGEX);
    private static final Logger LOGGER = LoggerFactory.getLogger(IdMatcher.class);
    @Autowired
    private FoundryObjMapper foundryObjMapper;
    @Autowired
    private AppSettings appSettings;

    @Override
    public List<Report> call() throws Exception {

        IdMatcherContext context = new IdMatcherContext(foundryObjMapper.getMapper());
        jsonStorage.streamAllItems()
                .forEach(object -> recordUsedIds(context, object));
        Report report = evalContext(context);
        LOGGER.info("report: {}", report.generate());
        return List.of(report);
    }

    private Report evalContext(IdMatcherContext context) {
        Report report = new Report(this, "Used but undeclared ids:\n");
        List<String> declaredIds = context.getDeclaredIds();
        Set<String> unusedIds = new HashSet<>(context.getDeclaredIds());
        for (Map.Entry<String, Set<String>> entry : context.getUsedIds().entrySet()) {
            List<String> usedButMissingIds = entry.getValue().stream()
                    .peek(unusedIds::remove)
                    .filter(s -> !declaredIds.contains(s))
                    .toList();
            if (!usedButMissingIds.isEmpty()) {
                report.add(String.format("%s used ids which are not in pack: %s\n", entry.getKey(), String.join(", ", usedButMissingIds)));
            }
        }

        Set<String> unusedClassFeatIds = new HashSet<>();
        Set<String> unusedEffects = new HashSet<>();
        for (String unusedId : Set.copyOf(unusedIds)) {
            BaseFoundryVttObject byId = jsonStorage.getById(unusedId);
            Set<String> referenceItems = Set.of("weapon", "equipment", "consumable", "tool", "loot", "spell", "backpack");
            if (byId instanceof SystemItem item && referenceItems.contains(item.getType())) {
                unusedIds.remove(unusedId);
            } else if (byId instanceof SystemItem item && "feat".equals(item.getType())) {
                SystemInformation systemInformation = item.getSystemInformation();
                String featType = systemInformation.getType().get("value");
                if ("class".equals(featType)) {
                    unusedClassFeatIds.add(unusedId);
                } else if ("feat".equals(featType)) {
                    unusedIds.remove(unusedId);
                }
            } else if (byId instanceof Effect effect) {
                unusedEffects.add(effect.getId());
            }
        }
        unusedIds.removeAll(unusedClassFeatIds);

        report.add("Declared but unused Class feat ids: " + String.join(", ", unusedClassFeatIds) + "\n");
        moveClassFeatures(unusedClassFeatIds);
        report.add("Declared but unused ids: " + String.join(", ", unusedIds) + "\n");
        report.add("Declared but unused effects: " + String.join(", ", unusedEffects) + "\n");
        moveEffects(unusedEffects);

        return report;
    }

    private void moveEffects(Set<String> unusedEffects) {
        Path subDir = appSettings.getBaseJsonSrcPath().resolve("effects/unused/");
        try {
            Files.createDirectories(subDir);
            for (String unusedClassFeatId : unusedEffects) {
                BaseFoundryVttObject byId = jsonStorage.getById(unusedClassFeatId);
                String fileName = byId.getSrcPath().toFile().getName();
                Path newPath = subDir.resolve(fileName);
                Files.move(byId.getSrcPath(), newPath);
                byId.setSrcPath(newPath);
            }
        } catch (NoSuchFileException e) {
            LOGGER.error("File not found: ", e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void moveClassFeatures(Set<String> unusedClassFeatIds) {
        Path subDir = appSettings.getBaseJsonSrcPath().resolve("features-class/unused/");
        try {
            Files.createDirectories(subDir);
            for (String unusedClassFeatId : unusedClassFeatIds) {
                BaseFoundryVttObject byId = jsonStorage.getById(unusedClassFeatId);
                String fileName = byId.getSrcPath().toFile().getName();
                Path newPath = subDir.resolve(fileName);
                Files.move(byId.getSrcPath(), newPath);
                byId.setSrcPath(newPath);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void recordUsedIds(IdMatcherContext context, BaseFoundryVttObject object) {
        Object objToCheck = object;
        if (Objects.requireNonNull(object) instanceof SystemItem systemItem) {
            if (Set.of("class", "subclass", "background").contains(systemItem.getType())) {
                objToCheck = systemItem.getSystemInformation().getDescription().getValue();
            }
        }
        ObjectMapper mapper = context.getMapper();
        try {
            String json = mapper.writeValueAsString(objToCheck);
            Set<String> ids = findIds(object.getId(), json);
            context.addDeclaredId(object.getId());
            context.addUsedIds(object.getId(), ids);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    Set<String> findIds(String originId, String json) {
        Set<String> ids = new HashSet<>();
        Matcher matcher = pattern.matcher(json);
        while (matcher.find()) {
            String foundId = matcher.group();
            if (!foundId.equals(originId)) ids.add(foundId);
        }
        return ids;
    }

    @Override
    public int getOrder() {
        return DataScrubber.ORDER_ID_MATCHER;
    }

    private static class IdMatcherContext {
        private final ObjectMapper mapper;
        Map<String, Set<String>> usedIds = new HashMap<>();
        List<String> declaredIds = new LinkedList<>();

        public IdMatcherContext(ObjectMapper mapper) {
            this.mapper = mapper;
        }

        public ObjectMapper getMapper() {
            return mapper;
        }

        public Map<String, Set<String>> getUsedIds() {
            return usedIds;
        }

        public List<String> getDeclaredIds() {
            return declaredIds;
        }

        public void addDeclaredId(String id) {
            declaredIds.add(id);
        }

        public void addUsedIds(String id, Set<String> ids) {
            usedIds.put(id, ids);
        }
    }
}
