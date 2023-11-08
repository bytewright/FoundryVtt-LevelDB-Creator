package org.bytewright.foundrytools.util;

import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Locale;
import java.util.Random;

@Service
public class IdGenerator {
    /**
     * should be short, keep it below 6 chars
     */
    public static final String GLOBAL_ID_PREFIX = "ABC";
    public static final String ID_PATTERN_REGEX = String.format(GLOBAL_ID_PREFIX + "[A-Za-z0-9]{%d}", 16 - GLOBAL_ID_PREFIX.length());
    private static final String digits = "0123456789";
    private static final String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String lower = upper.toLowerCase(Locale.ROOT);
    private static final String alphanum = upper + lower + digits;
    private final Random random = new SecureRandom();
    private final Random grantIdRandom = new SecureRandom("42".getBytes(StandardCharsets.UTF_8));

    public String generateId() {
        return generateId("");
    }

    public String generateId(String s) {
        char[] buf = new char[16];
        for (int i = 0; i < GLOBAL_ID_PREFIX.length(); i++) {
            buf[i] = GLOBAL_ID_PREFIX.charAt(i);
        }
        char[] symbols = alphanum.toCharArray();
        for (int idx = 3; idx < buf.length; ++idx)
            buf[idx] = symbols[random.nextInt(symbols.length)];
        return new String(buf);
    }

    /**
     * Generate stable ids for grant of advancements
     */
    public String generateGrantId() {
        char[] buf = new char[16];
        for (int i = 0; i < GLOBAL_ID_PREFIX.length(); i++) {
            buf[i] = GLOBAL_ID_PREFIX.charAt(i);
        }
        char[] symbols = alphanum.toCharArray();
        for (int idx = 3; idx < buf.length; ++idx)
            buf[idx] = symbols[grantIdRandom.nextInt(symbols.length)];
        return new String(buf);
    }

    public String[] generatePrefixedRndFilenames(int size) {
        String[] ids = new String[size];
        char[] commonName = new char[12];
        int prefixLen = GLOBAL_ID_PREFIX.length();
        for (int i = 0; i < prefixLen; i++) {
            commonName[i] = GLOBAL_ID_PREFIX.charAt(i);
        }
        commonName[prefixLen + 1] = 'E';
        commonName[prefixLen + 2] = 'f';
        commonName[prefixLen + 3] = 'f';
        commonName[prefixLen + 4] = '_';
        char[] symbols = alphanum.toCharArray();
        for (int idx = prefixLen + 5; idx < commonName.length; ++idx)
            commonName[idx] = symbols[random.nextInt(symbols.length)];
        for (int i = 0; i < size; i++) {
            ids[i] = new String(commonName) + String.format("%04d", i);
        }
        return ids;
    }
}
