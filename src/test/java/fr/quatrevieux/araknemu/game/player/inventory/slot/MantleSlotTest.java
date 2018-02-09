package fr.quatrevieux.araknemu.game.player.inventory.slot;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.living.entity.player.PlayerItem;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.item.ItemService;
import fr.quatrevieux.araknemu.game.player.inventory.InventoryEntry;
import fr.quatrevieux.araknemu.game.world.item.inventory.exception.InventoryException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class MantleSlotTest extends GameBaseCase {
    private MantleSlot slot;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushItemTemplates();

        slot = new MantleSlot();
    }

    @Test
    void checkBadType() throws ContainerException {
        assertFalse(slot.check(container.get(ItemService.class).create(2425), 1));
    }

    @Test
    void checkBadQuantity() throws ContainerException {
        assertFalse(slot.check(container.get(ItemService.class).create(2414), 10));
    }

    @Test
    void checkAlreadySet() throws ContainerException {
        slot.uncheckedSet(new InventoryEntry(null, null, null));

        assertFalse(slot.check(container.get(ItemService.class).create(2414), 1));
    }

    @Test
    void checkSuccess() throws ContainerException {
        assertTrue(slot.check(container.get(ItemService.class).create(2414), 1));
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
        InventoryEntry entry = new InventoryEntry(null, new PlayerItem(1, 1, 2414, new ArrayList<>(), 1, -1), container.get(ItemService.class).create(2414));

        slot.set(entry);

        assertSame(entry, slot.entry());
    }
}
