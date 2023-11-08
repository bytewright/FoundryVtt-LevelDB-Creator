package org.bytewright.foundrytools.datascrubbing;

import org.bytewright.foundrytools.json.JsonStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;

import java.util.List;
import java.util.concurrent.Callable;

public abstract class DataScrubber implements Callable<List<Report>>, Ordered {
    protected static final int ORDER_DEFAULT = 100_000;
    protected static final int ORDER_DESC_FORMATTER = 100_100;
    protected static final int ORDER_ID_MATCHER = 100_200;
    protected static final int ORDER_IMPORT_ID_FIXER = 100_300;
    @Autowired
    protected JsonStorage jsonStorage;

    @Override
    public List<Report> call() throws Exception {
        return run();
    }

    public List<Report> run() {
        return List.of(new Report(this, "Nothing to report"));
    }
}
