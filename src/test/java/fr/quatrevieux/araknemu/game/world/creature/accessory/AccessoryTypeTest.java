package fr.quatrevieux.araknemu.game.world.creature.accessory;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AccessoryTypeTest {
    @Test
    void bySlot() {
        assertEquals(AccessoryType.WEAPON, AccessoryType.bySlot(1));
        assertEquals(AccessoryType.HELMET, AccessoryType.bySlot(6));
        assertEquals(AccessoryType.MANTLE, AccessoryType.bySlot(7));
        assertEquals(AccessoryType.PET, AccessoryType.bySlot(8));
        assertEquals(AccessoryType.SHIELD, AccessoryType.bySlot(15));
    }

    @Test
    void isAccessorySlot() {
        assertTrue(AccessoryType.isAccessorySlot(1));
        assertTrue(AccessoryType.isAccessorySlot(6));
        assertFalse(AccessoryType.isAccessorySlot(-1));
        assertFalse(AccessoryType.isAccessorySlot(0));
    }

    @Test
    void slots() {
        assertArrayEquals(
            new int[] {1, 6, 7, 8, 15},
            AccessoryType.slots()
        );
    }
}