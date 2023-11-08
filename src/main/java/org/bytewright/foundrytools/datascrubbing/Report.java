package org.bytewright.foundrytools.datascrubbing;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public record Report(Object src, List<String> reportLines) {

    public Report(Object src, String report) {
        this(src, new LinkedList<>(List.of(report)));
    }

    public Report add(String reportLine) {
        reportLines.add(reportLine);
        return this;
    }

    @Override
    public String toString() {
        return "Report{" +
                "src=" + src.getClass().getName() +
                ", reportLines=" + reportLines +
                '}';
    }

    public String generate() {
        return String.join("", reportLines);
    }
}
