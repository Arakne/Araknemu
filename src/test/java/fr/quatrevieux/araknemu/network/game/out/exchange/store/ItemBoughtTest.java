package fr.quatrevieux.araknemu.network.game.out.exchange.store;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ItemBoughtTest {
    @Test
    void generateSuccess() {
        assertEquals("EBK", ItemBought.success().toString());
    }

    @Test
    void generateFailed() {
        assertEquals("EBE", ItemBought.failed().toString());
    }
}
