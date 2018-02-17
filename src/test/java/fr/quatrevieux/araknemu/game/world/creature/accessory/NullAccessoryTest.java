package fr.quatrevieux.araknemu.game.world.creature.accessory;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NullAccessoryTest {
    @Test
    void data() {
        NullAccessory accessory = new NullAccessory(AccessoryType.HELMET);

        assertEquals(AccessoryType.HELMET, accessory.type());
        assertEquals(0, accessory.appearance());
        assertNull(accessory.itemType());
        assertEquals(0, accessory.frame());
        assertEquals("", accessory.toString());
    }
}