package org.bytewright.foundrytools.datageneration;

import org.bytewright.foundrytools.datascrubbing.Report;
import org.bytewright.foundrytools.json.JsonStorage;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public abstract class DataGenerator {

    @Autowired
    protected JsonStorage jsonStorage;
    public List<Report> run() {
        return generateData();
    }

    protected abstract List<Report> generateData();
}
