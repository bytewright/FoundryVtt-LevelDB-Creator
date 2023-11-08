package org.bytewright.foundrytools.datascrubbing;

import org.bytewright.foundrytools.json.pojo.Description;
import org.bytewright.foundrytools.json.pojo.SystemInformation;
import org.bytewright.foundrytools.json.pojo.SystemItem;
import org.bytewright.foundrytools.util.DescTextFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DescriptionHtmlFormatter extends DataScrubber {
    private static final Logger LOGGER = LoggerFactory.getLogger(DescriptionHtmlFormatter.class);

    @Override
    public List<Report> run() {
        Report report = new Report(this, "Reformatted desc of Items: ");
        jsonStorage.streamAll(SystemItem.class)
                .forEach(systemItem -> scrubDescription(report, systemItem));
        return List.of(report);
    }

    private void scrubDescription(Report report, SystemItem systemItem) {
        if (systemItem.getSystemInformation() == null) {
            return;
        }
        SystemInformation systemInformation = systemItem.getSystemInformation();
        Description description = systemInformation.getDescription();
        String value = description.getValue();

        String cleanedDesc = DescTextFormatter.clean(value);

        if (!value.equals(cleanedDesc)) {
            description.setValue(cleanedDesc);
            report.add(systemItem.getId() + "; ");
        }
    }

    @Override
    public int getOrder() {
        return ORDER_DESC_FORMATTER;
    }
}
