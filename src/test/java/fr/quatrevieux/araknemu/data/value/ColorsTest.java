package fr.quatrevieux.araknemu.data.value;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ColorsTest {
    @Test
    void toArray() {
        Colors colors = new Colors(123, 456, 789);

        assertArrayEquals(new int[] {123, 456, 789}, colors.toArray());
    }

    @Test
    void toHexArray() {
        Colors colors = new Colors(123, 456, 789);

        assertArrayEquals(new String[] {"7b", "1c8", "315"}, colors.toHexArray());
    }

    @Test
    void toHexArrayDefaultColors() {
        Colors colors = new Colors(-1, -1, 789);

        assertArrayEquals(new String[] {"-1", "-1", "315"}, colors.toHexArray());
    }
}
