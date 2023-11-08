package org.bytewright.foundrytools.datascrubbing;

import org.bytewright.foundrytools.json.pojo.SystemInformation;
import org.bytewright.foundrytools.json.pojo.SystemItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SourceFieldScrubber extends DataScrubber {
    private static final Logger LOGGER = LoggerFactory.getLogger(SourceFieldScrubber.class);

    @Override
    public int getOrder() {
        return DataScrubber.ORDER_DEFAULT;
    }

    @Override
    public List<Report> run() {
        return jsonStorage.streamAll(SystemItem.class)
                .map(this::scrubSource)
                .flatMap(Optional::stream)
                .map(s -> new Report(this, String.format("Item without source: %s", s)))
                .toList();
    }

    private Optional<String> scrubSource(SystemItem systemItem) {
        if (systemItem.getSystemInformation() == null) {
            return Optional.empty();
        }
        SystemInformation systemInformation = systemItem.getSystemInformation();
        if (systemInformation.getSource() == null) {
            return Optional.ofNullable(systemItem.getId());
        } else if (isPhb(systemInformation)) {
            systemInformation.setSource("Player's Handbook");
        } else if (isDmg(systemInformation)) {
            systemInformation.setSource("Dungeon Master's Guide");
        } else if (isOfficalAdventure(systemInformation)) {
        } else if (isXge(systemInformation)) {
            systemInformation.setSource("Xanathar's Guide to Everything");
        } else if (isTce(systemInformation)) {
            systemInformation.setSource("Tasha's Cauldron of Everything");
        } else if (isFtod(systemInformation)) {
            systemInformation.setSource("Fizban's Treasury of Dragons");
        } else if (isEGtW(systemInformation)) {
            systemInformation.setSource("Explorer's Guide to Wildemount");
        } else if (isEEPC(systemInformation)) {
            systemInformation.setSource("Elemental Evil Player's Companion");
        } else if (isErlw(systemInformation)) {
            systemInformation.setSource("Eberron: Rising from the Last War");
        } else if (isScag(systemInformation)) {
            systemInformation.setSource("Sword Coast Adventurer's Guide");
        } else if (isSRD(systemInformation)) {
            systemInformation.setSource("SRD 5.1");
        } else {
            LOGGER.debug("Unknown Source: {}", systemInformation.getSource());
        }
        return Optional.empty();
    }

    private boolean isErlw(SystemInformation systemInformation) {
        String src = systemInformation.getSource().toLowerCase();
        return src.startsWith("Eberron: Rising from the Last War".toLowerCase());
    }

    private boolean isScag(SystemInformation systemInformation) {
        String src = systemInformation.getSource().toLowerCase();
        return src.startsWith("scag") ||
                src.startsWith("Sword Coast Adventurer's Guide".toLowerCase());
    }

    private boolean isEEPC(SystemInformation systemInformation) {
        String src = systemInformation.getSource().toLowerCase();
        return src.startsWith("Elemental Evil Player's Companion".toLowerCase());
    }

    private boolean isEGtW(SystemInformation systemInformation) {
        String src = systemInformation.getSource().toLowerCase();
        return src.startsWith("Explorer's Guide to Wildemount".toLowerCase());
    }

    private boolean isOfficalAdventure(SystemInformation systemInformation) {
        String src = systemInformation.getSource().toLowerCase();
        return src.startsWith("Mythic Odysseys of Theros".toLowerCase());
    }

    private boolean isDmg(SystemInformation systemInformation) {
        String src = systemInformation.getSource().toLowerCase();
        return src.startsWith("dmg") ||
                src.startsWith("Dungeon Master's Guide".toLowerCase());
    }

    private boolean isSRD(SystemInformation systemInformation) {
        return "SRD 5.1".equalsIgnoreCase(systemInformation.getSource());
    }

    private boolean isFtod(SystemInformation systemInformation) {
        String src = systemInformation.getSource().toLowerCase();
        return src.startsWith("ftod") ||
                src.startsWith("Fizban's Treasury of Dragons".toLowerCase());
    }

    private boolean isTce(SystemInformation systemInformation) {
        String src = systemInformation.getSource().toLowerCase();
        return src.startsWith("tce") ||
                src.startsWith("Tasha's Cauldron of Everything".toLowerCase());
    }

    private boolean isXge(SystemInformation systemInformation) {
        String src = systemInformation.getSource().toLowerCase();
        return src.startsWith("xge") ||
                src.startsWith("Xanathar's Guide to Everything".toLowerCase());
    }

    private boolean isPhb(SystemInformation systemInformation) {
        String src = systemInformation.getSource().toLowerCase();
        return src.startsWith("phb") ||
                src.startsWith("Player's Handbook".toLowerCase());
    }
}
