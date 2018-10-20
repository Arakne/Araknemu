package fr.quatrevieux.araknemu.data.value;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IntervalTest {
    @Test
    void contains() {
        Interval interval = new Interval(5, 10);

        assertTrue(interval.contains(5));
        assertTrue(interval.contains(10));
        assertTrue(interval.contains(7));

        assertFalse(interval.contains(2));
        assertFalse(interval.contains(11));
    }

    @Test
    void modify() {
        Interval interval = new Interval(5, 10);

        assertSame(interval, interval.modify(0));
        assertEquals(new Interval(5, 15), interval.modify(5));
        assertEquals(new Interval(5, 7), interval.modify(-3));
        assertEquals(new Interval(5, 5), interval.modify(-10));

        interval = new Interval(5, 5);

        assertSame(interval, interval.modify(-2));
        assertEquals(new Interval(5, 7), interval.modify(2));
    }

    @Test
    void string() {
        assertEquals("[5, 7]", new Interval(5, 7).toString());
    }

    @Test
    void equals() {
        assertEquals(
            new Interval(5, 7),
            new Interval(5, 7)
        );

        assertNotEquals(
            new Interval(5, 7),
            new Interval(5, 6)
        );

        assertNotEquals(
            new Interval(4, 7),
            new Interval(5, 7)
        );

        assertNotEquals(
            new Interval(4, 7),
            new Object()
        );
    }

    @Test
    void hash() {
        assertEquals(new Interval(5, 7).hashCode(), new Interval(5, 7).hashCode());
        assertNotEquals(new Interval(5, 7).hashCode(), new Interval(4, 7).hashCode());
    }
}
