package fr.quatrevieux.araknemu.data.value;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PositionTest {
    @Test
    void data() {
        Position position = new Position(1254, 235);

        assertEquals(1254, position.map());
        assertEquals(235, position.cell());
        assertFalse(position.isNull());
    }

    @Test
    void equals() {
        Position position = new Position(1254, 235);

        assertNotEquals(null, position);
        assertNotEquals(new Position(74, 52), position);
        assertEquals(position, position);
        assertEquals(new Position(1254, 235), position);
    }

    @Test
    void newCell() {
        Position position = new Position(1254, 235);

        assertEquals(241, position.newCell(241).cell());
        assertNotSame(position, position.newCell(241).cell());
    }

    @Test
    void generateString() {
        assertEquals("(1254, 235)", new Position(1254, 235).toString());
    }
}