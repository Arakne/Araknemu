package fr.quatrevieux.araknemu.network.game.out;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QuickPongTest {
    @Test
    void generate() {
        assertEquals("qpong", new QuickPong().toString());
    }
}