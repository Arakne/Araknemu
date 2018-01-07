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
}
