package fr.quatrevieux.araknemu.network.game.out;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PongTest {
    @Test
    void generate() {
        assertEquals("pong", new Pong().toString());
    }
}
