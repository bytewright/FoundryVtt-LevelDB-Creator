package org.bytewright.foundrytools.json;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class JsonStorageTest {
    @Test
    void testPackByFolderSplit() {
        String packDir = "foo.bar.subdir";
        String[] split = packDir.split("\\.");

        String[] strings = Arrays.copyOfRange(split, 1, split.length);
        Assertions.assertEquals("bar", strings[0]);
        Assertions.assertEquals("subdir", strings[1]);

        packDir = "foo.bar";
        split = packDir.split("\\.");

        strings = Arrays.copyOfRange(split, 1, split.length);
        Assertions.assertEquals("bar", strings[0]);
    }
}