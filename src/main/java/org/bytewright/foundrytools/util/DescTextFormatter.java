package org.bytewright.foundrytools.util;

public class DescTextFormatter {
    public static String clean(String value) {
        String cleanedDesc = value.replace(".\n", ".<NEWLINE>")
                .replace(":\n", ".<NEWLINE>")
                .replace("-\n", "")
                .replace("\u00AD\n", "")
                .replace("\r", "")
                .replace("\n", " ")
                .replace("/p> <p", "/p><p")
                .replace("<NEWLINE>", "</p><p>")
                .strip();
        if (!cleanedDesc.startsWith("<p>")) {
            cleanedDesc = "<p>" + cleanedDesc + "</p>";
        }
        return cleanedDesc;
    }

    public static String fileName(String name) {
        return name.replace(" ", "_")
                .replace(":", "");
    }
}
