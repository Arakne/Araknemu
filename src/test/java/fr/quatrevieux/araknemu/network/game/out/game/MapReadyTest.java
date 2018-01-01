package fr.quatrevieux.araknemu.network.game.out.game;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MapReadyTest {
    @Test
    void generate() {
        assertEquals("GDK", new MapReady().toString());
    }
}