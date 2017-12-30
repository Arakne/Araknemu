package fr.quatrevieux.araknemu.network.game.out.game;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameCreationErrorTest {
    @Test
    void generate() {
        assertEquals("GCE", new GameCreationError().toString());
    }
}