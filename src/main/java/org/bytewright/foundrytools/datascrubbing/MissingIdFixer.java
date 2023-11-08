package org.bytewright.foundrytools.datascrubbing;

import org.bytewright.foundrytools.json.pojo.SystemItem;
import org.bytewright.foundrytools.util.IdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MissingIdFixer extends DataScrubber {
    private static final Logger LOGGER = LoggerFactory.getLogger(MissingIdFixer.class);

    @Autowired
    private IdGenerator idGenerator;

    @Override
    public List<Report> run() {
        return jsonStorage.streamAll(SystemItem.class)
                .map(this::addMissingIds)
                .flatMap(Optional::stream)
                .toList();
    }

    private Optional<Report> addMissingIds(SystemItem systemItem) {
        if (systemItem.getId() == null) {
            systemItem.setId(idGenerator.generateId());
            return Optional.of(new Report(this, String.format("Fixed missing id: %s", systemItem)));
        }
        return Optional.empty();
    }

    @Override
    public int getOrder() {
        return DataScrubber.ORDER_DEFAULT;
    }
}
