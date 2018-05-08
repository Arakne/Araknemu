package fr.quatrevieux.araknemu.game.world.map;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DirectionTest {
    @Test
    void toChar() {
        assertEquals('e', Direction.WEST.toChar());
        assertEquals('a', Direction.EAST.toChar());
    }

    @Test
    void byChar() {
        assertEquals(Direction.WEST, Direction.byChar('e'));
        assertEquals(Direction.SOUTH, Direction.byChar('c'));
    }

    @Test
    void opposite() {
        assertEquals(Direction.WEST, Direction.EAST.opposite());
        assertEquals(Direction.NORTH, Direction.SOUTH.opposite());
    }

    @Test
    void restricted() {
        assertFalse(Direction.WEST.restricted());
        assertFalse(Direction.NORTH.restricted());
        assertFalse(Direction.EAST.restricted());
        assertFalse(Direction.SOUTH.restricted());

        assertTrue(Direction.SOUTH_WEST.restricted());
        assertTrue(Direction.NORTH_WEST.restricted());
        assertTrue(Direction.NORTH_EAST.restricted());
        assertTrue(Direction.SOUTH_EAST.restricted());
    }

    @Test
    void nextCellIncrement() {
        assertEquals(1, Direction.EAST.nextCellIncrement(15));
        assertEquals(15, Direction.SOUTH_EAST.nextCellIncrement(15));
        assertEquals(29, Direction.SOUTH.nextCellIncrement(15));
        assertEquals(14, Direction.SOUTH_WEST.nextCellIncrement(15));
        assertEquals(-1, Direction.WEST.nextCellIncrement(15));
        assertEquals(-15, Direction.NORTH_WEST.nextCellIncrement(15));
        assertEquals(-29, Direction.NORTH.nextCellIncrement(15));
        assertEquals(-14, Direction.NORTH_EAST.nextCellIncrement(15));
    }
}
