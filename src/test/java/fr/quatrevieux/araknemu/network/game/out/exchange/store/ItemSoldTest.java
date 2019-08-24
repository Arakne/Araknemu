package fr.quatrevieux.araknemu.network.game.out.exchange.store;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ItemSoldTest {
    @Test
    void generateSuccess() {
        assertEquals("ESK", ItemSold.success().toString());
    }

    @Test
    void generateFailed() {
        assertEquals("ESE", ItemSold.failed().toString());
    }
}