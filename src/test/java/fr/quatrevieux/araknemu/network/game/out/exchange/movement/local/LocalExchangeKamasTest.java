package fr.quatrevieux.araknemu.network.game.out.exchange.movement.local;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LocalExchangeKamasTest {
    @Test
    void generate() {
        assertEquals("EMKG12345", new LocalExchangeKamas(12345).toString());
    }
}