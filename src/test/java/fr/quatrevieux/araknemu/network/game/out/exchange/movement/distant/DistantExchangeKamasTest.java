package fr.quatrevieux.araknemu.network.game.out.exchange.movement.distant;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DistantExchangeKamasTest {
    @Test
    void generate() {
        assertEquals("EmKG12345", new DistantExchangeKamas(12345).toString());
    }
}