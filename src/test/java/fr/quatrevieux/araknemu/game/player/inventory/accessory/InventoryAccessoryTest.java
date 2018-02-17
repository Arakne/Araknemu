package fr.quatrevieux.araknemu.game.player.inventory.accessory;

import fr.quatrevieux.araknemu.data.living.entity.player.PlayerItem;
import fr.quatrevieux.araknemu.game.player.inventory.InventoryEntry;
import fr.quatrevieux.araknemu.game.world.creature.accessory.AccessoryType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InventoryAccessoryTest {
    @Test
    void data() {
        InventoryAccessory accessory = new InventoryAccessory(
            new InventoryEntry(null, new PlayerItem(1, 1, 123, null, 0, 6), null)
        );

        assertEquals(AccessoryType.HELMET, accessory.type());
        assertEquals(123, accessory.appearance());
        assertNull(accessory.itemType());
        assertEquals(0, accessory.frame());
        assertEquals("7b", accessory.toString());
    }
}
