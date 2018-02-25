package fr.quatrevieux.araknemu.game.player.inventory.slot;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.living.entity.player.PlayerItem;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.item.ItemService;
import fr.quatrevieux.araknemu.game.player.inventory.InventoryEntry;
import fr.quatrevieux.araknemu.game.world.item.inventory.ItemStorage;
import fr.quatrevieux.araknemu.game.world.item.inventory.exception.InventoryException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class UsableSlotTest extends GameBaseCase {
    private UsableSlot slot;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet
            .pushItemTemplates()
            .pushUsableItems()
        ;

        slot = new UsableSlot(Mockito.mock(ItemStorage.class), 40);
    }

    @Test
    void checkBadClass() throws ContainerException {
        assertFalse(slot.check(container.get(ItemService.class).create(2425), 1));
    }

    @Test
    void checkAlreadySet() throws ContainerException {
        slot.uncheckedSet(new InventoryEntry(null, null, null));

        assertFalse(slot.check(container.get(ItemService.class).create(800), 10));
    }

    @Test
    void checkSuccess() throws ContainerException {
        assertTrue(slot.check(container.get(ItemService.class).create(468), 100));
    }

    @Test
    void setFail() {
        assertThrows(InventoryException.class, () -> slot.set(
            new InventoryEntry(null, new PlayerItem(1, 1, 2425, new ArrayList<>(), 1, -1), container.get(ItemService.class).create(2425)
        )));

        assertNull(slot.entry());
    }

    @Test
    void setSuccess() throws ContainerException, InventoryException {
        InventoryEntry entry = new InventoryEntry(null, new PlayerItem(1, 1, 468, new ArrayList<>(), 100, -1), container.get(ItemService.class).create(468));

        slot.set(entry);

        assertSame(entry, slot.entry());
    }

    @Test
    void hasEquipment() {
        assertFalse(slot.hasEquipment());
    }
}