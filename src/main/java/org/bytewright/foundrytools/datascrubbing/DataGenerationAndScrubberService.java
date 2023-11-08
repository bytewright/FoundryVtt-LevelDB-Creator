package org.bytewright.foundrytools.datascrubbing;

import org.bytewright.foundrytools.datageneration.DataGenerator;
import org.bytewright.foundrytools.event.DataScrubbingFinishedEvent;
import org.bytewright.foundrytools.event.FullContentLoadFinishedEvent;
import org.bytewright.foundrytools.json.JsonStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.core.OrderComparator;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
public class DataGenerationAndScrubberService implements ApplicationListener<FullContentLoadFinishedEvent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataGenerationAndScrubberService.class);

    @Autowired
    private ApplicationEventPublisher eventPublisher;
    @Autowired
    private JsonStorage jsonStorage;
    private List<DataGenerator> dataGenerators = new LinkedList<>();
    private List<DataScrubber> dataScrubberList = new LinkedList<>();

    @Autowired
    public void setDataGenerators(List<DataGenerator> dataGenerators) {
        this.dataGenerators = dataGenerators.stream().sorted(OrderComparator.INSTANCE).toList();
    }

    @Autowired
    public void setDataScrubberList(List<DataScrubber> dataScrubberList) {
        this.dataScrubberList = dataScrubberList.stream().sorted(OrderComparator.INSTANCE).toList();
    }

    @Override
    public void onApplicationEvent(FullContentLoadFinishedEvent event) {
        try {
            List<Report> reports = new LinkedList<>();
            LOGGER.info("All data seems to be loaded, starting to clean and polish data...");

            for (DataGenerator dataGenerator : dataGenerators) {
                LOGGER.info("Executing DataGenerator: {}", dataGenerator);
                reports.addAll(dataGenerator.run());
            }

            for (DataScrubber dataScrubber : dataScrubberList) {
                LOGGER.info("Executing DataScrubber: {}", dataScrubber);
                reports.addAll(dataScrubber.call());
            }

            for (Report report : reports) {
                LOGGER.debug("Service Report: {}", report);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        eventPublisher.publishEvent(new DataScrubbingFinishedEvent(this));
    }
}
