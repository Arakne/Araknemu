package fr.quatrevieux.araknemu.game.player.inventory.slot;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.item.inventory.exception.InventoryException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NullSlotTest extends GameBaseCase {
    private NullSlot slot;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        slot = new NullSlot(1);
    }

    @Test
    void entry() {
        assertFalse(slot.entry().isPresent());
    }

    @Test
    void equipment() {
        assertFalse(slot.equipment().isPresent());
    }

    @Test
    void id() {
        assertEquals(1, slot.id());
    }

    @Test
    void check() {
        assertThrows(InventoryException.class, () -> slot.check(null, 1));
    }

    @Test
    void set() {
        assertThrows(InventoryException.class, () -> slot.set(null));
    }
}
