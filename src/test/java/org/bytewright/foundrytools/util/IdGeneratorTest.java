package org.bytewright.foundrytools.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

class IdGeneratorTest {

    @Test
    void generateId() {
        IdGenerator idGenerator = new IdGenerator();
        IntStream.range(0, 20)
                .mapToObj(value -> idGenerator.generateId())
                .forEach(System.out::println);
        Assertions.assertEquals(16, idGenerator.generateId().length());
    }

    @Test
    void name() {
        IdGenerator idGenerator = new IdGenerator();
        String[] strings = idGenerator.generatePrefixedRndFilenames(5);
        assertThat(strings).hasSize(4);
    }
}