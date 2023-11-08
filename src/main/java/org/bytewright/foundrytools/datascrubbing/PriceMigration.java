package org.bytewright.foundrytools.datascrubbing;

import org.bytewright.foundrytools.json.pojo.SystemInformation;
import org.bytewright.foundrytools.json.pojo.SystemItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PriceMigration extends DataScrubber {
    private static final Logger LOGGER = LoggerFactory.getLogger(PriceMigration.class);

    @Override
    public int getOrder() {
        return DataScrubber.ORDER_DEFAULT;
    }

    @Override
    public List<Report> run() {
        return jsonStorage.streamAll(SystemItem.class)
                .map(this::scrubPrices)
                .flatMap(Optional::stream)
                .toList();
    }

    private Optional<Report> scrubPrices(SystemItem systemItem) {
        if (systemItem.getSystemInformation() == null) {
            return Optional.empty();
        }
        SystemInformation systemInformation = systemItem.getSystemInformation();
        if (systemInformation.any().containsKey("price")) {
            Object price = systemInformation.any().get("price");
            if (price instanceof Number) {
                systemInformation.any().put("price", newPriceModel((Number) price));
                return Optional.of(new Report(this, String.format("Migrated price: %s", systemItem)));
            }
        }
        return Optional.empty();
    }

    private Object newPriceModel(Number price) {
        return Map.of("value", price,
                "denomination", "gp");
    }
}
