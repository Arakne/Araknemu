package fr.quatrevieux.araknemu.util;

import fr.quatrevieux.araknemu._test.TestCase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RandomUtilTest extends TestCase {
    private RandomUtil util = new RandomUtil();

    @Test
    void randFixed() {
        assertEquals(15, util.rand(15, 0));
    }

    @Test
    void randStartWithZero() {
        assertBetween(0, 15, util.rand(0, 15));
    }

    @Test
    void randInterval() {
        for (int i = 0; i < 100; ++i) {
            assertBetween(25, 45, util.rand(25, 45));
        }
    }

    @Test
    void randArrayOneElement() {
        assertEquals(32, util.rand(new int[] {32}));
    }

    @Test
    void randArrayFixed() {
        assertEquals(32, util.rand(new int[] {32, 0}));
    }

    @Test
    void randArray() {
        assertBetween(12, 23, util.rand(new int[] {12, 23}));
    }
}
