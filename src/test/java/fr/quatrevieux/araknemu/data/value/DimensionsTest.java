package fr.quatrevieux.araknemu.data.value;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DimensionsTest {
    @Test
    void data() {
        Dimensions dimensions = new Dimensions(15, 17);

        assertEquals(17, dimensions.height());
        assertEquals(15, dimensions.width());
    }

    @Test
    void equals() {
        assertNotEquals(null, new Dimensions(12, 15));
        assertNotEquals(new Dimensions(15, 12), new Dimensions(12, 15));
        assertEquals(new Dimensions(12, 15), new Dimensions(12, 15));
    }
}