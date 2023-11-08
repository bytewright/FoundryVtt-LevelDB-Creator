package org.bytewright.foundrytools.util;

import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;

public class JacksonPrettyPrinter {
    public static final PrettyPrinter INSTANCE = new DefaultPrettyPrinter()
            .withArrayIndenter(new DefaultIndenter())
            .withoutSpacesInObjectEntries();
}
