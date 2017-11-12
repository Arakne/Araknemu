package fr.quatrevieux.araknemu.util;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class RandomStringUtilTest {
    @Test
    void neverSameValue() {
        RandomStringUtil generator = new RandomStringUtil(new Random(), "abcd");
        assertNotEquals(generator.generate(20), generator.generate(20));
    }

    @Test
    void generatedLength() {
        assertEquals(32, new RandomStringUtil(new Random(), "abcd").generate(32).length());
    }

    @Test
    void charset() {
        assertEquals("aaaa", new RandomStringUtil(new Random(), "a").generate(4));
    }
}
