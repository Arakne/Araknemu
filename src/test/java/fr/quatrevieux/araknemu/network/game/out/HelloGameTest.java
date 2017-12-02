package fr.quatrevieux.araknemu.network.game.out;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HelloGameTest {
    @Test
    void generate() {
        assertEquals("HG", new HelloGame().toString());
    }
}