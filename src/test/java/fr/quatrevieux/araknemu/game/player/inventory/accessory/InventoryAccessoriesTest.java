package fr.quatrevieux.araknemu.game.player.inventory.accessory;

import fr.quatrevieux.araknemu.data.living.entity.player.PlayerItem;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.event.DefaultListenerAggregate;
import fr.quatrevieux.araknemu.game.item.ItemService;
import fr.quatrevieux.araknemu.game.player.inventory.InventoryEntry;
import fr.quatrevieux.araknemu.game.player.inventory.slot.InventorySlots;
import fr.quatrevieux.araknemu.game.world.creature.accessory.Accessory;
import fr.quatrevieux.araknemu.game.world.creature.accessory.AccessoryType;
import fr.quatrevieux.araknemu.game.world.creature.accessory.NullAccessory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InventoryAccessoriesTest extends GameBaseCase {
    private InventoryAccessories accessories;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushItemTemplates();

        InventorySlots slots = new InventorySlots(new DefaultListenerAggregate());

        slots.get(1).uncheckedSet(new InventoryEntry(null, new PlayerItem(0, 0, 2416, null, 1, 1), container.get(ItemService.class).create(2416)));
        slots.get(6).uncheckedSet(new InventoryEntry(null, new PlayerItem(0, 0, 2411, null, 1, 0), container.get(ItemService.class).create(2411)));

        accessories = new InventoryAccessories(slots);
    }

    @Test
    void getAccessory() {
        Accessory accessory = accessories.get(AccessoryType.WEAPON);

        assertInstanceOf(InventoryAccessory.class, accessory);
        assertEquals(AccessoryType.WEAPON, accessory.type());
        assertEquals(2416, accessory.appearance());
    }

    @Test
    void getNotEquiped() {
        assertInstanceOf(NullAccessory.class, accessories.get(AccessoryType.MANTLE));
    }

    @Test
    void all() {
        assertCount(5, accessories.all());

        assertEquals(2416, accessories.all().get(0).appearance());
        assertEquals(2411, accessories.all().get(1).appearance());
    }

    @Test
    void string() {
        assertEquals("970,96b,,,", accessories.toString());
    }
}
