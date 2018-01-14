package fr.quatrevieux.araknemu.network.game.out.basic;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NoopTest {
    @Test
    void generate() {
        assertEquals("BN", new Noop().toString());
    }
}
