package fr.quatrevieux.araknemu.game.player.inventory.slot;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DefaultSlotTest {
    private DefaultSlot slot = new DefaultSlot();

    @Test
    void getters() {
        assertNull(slot.entry());
        assertEquals(-1, slot.id());
        assertTrue(slot.check(null, 1));
    }
}
