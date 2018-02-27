package fr.quatrevieux.araknemu.game.player.inventory.slot;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.living.entity.player.PlayerItem;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.event.DefaultListenerAggregate;
import fr.quatrevieux.araknemu.game.item.ItemService;
import fr.quatrevieux.araknemu.game.player.inventory.InventoryEntry;
import fr.quatrevieux.araknemu.game.world.item.inventory.ItemStorage;
import fr.quatrevieux.araknemu.game.world.item.inventory.exception.BadLevelException;
import fr.quatrevieux.araknemu.game.world.item.inventory.exception.InventoryException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class DofusSlotTest extends GameBaseCase {
    private DofusSlot slot;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushItemTemplates();

        slot = new DofusSlot(new DefaultListenerAggregate(), Mockito.mock(ItemStorage.class), gamePlayer(), DofusSlot.SLOT_IDS[0]);
    }

    @Test
    void checkBadType() throws ContainerException {
        assertThrows(InventoryException.class, () -> slot.check(container.get(ItemService.class).create(2411), 1));
    }

    @Test
    void checkBadQuantity() throws ContainerException {
        assertThrows(InventoryException.class, () -> slot.check(container.get(ItemService.class).create(694), 10));
    }

    @Test
    void checkAlreadySet() throws ContainerException {
        slot.uncheckedSet(new InventoryEntry(null, null, null));

        assertThrows(InventoryException.class, () -> slot.check(container.get(ItemService.class).create(694), 1));
    }

    @Test
    void checkBadLevel() throws ContainerException, SQLException {
        dataSet.pushHighLevelItems();

        assertThrows(BadLevelException.class, () -> slot.check(container.get(ItemService.class).create(111694), 1));
    }

    @Test
    void checkSuccess() throws ContainerException, InventoryException {
        slot.check(container.get(ItemService.class).create(694), 1);
    }

    @Test
    void setFail() {
        assertThrows(InventoryException.class, () -> slot.set(
            new InventoryEntry(null, new PlayerItem(1, 1, 2411, new ArrayList<>(), 1, -1), container.get(ItemService.class).create(2411)
        )));

        assertNull(slot.entry());
    }

    @Test
    void setSuccess() throws ContainerException, InventoryException {
        InventoryEntry entry = new InventoryEntry(null, new PlayerItem(1, 1, 694, new ArrayList<>(), 1, -1), container.get(ItemService.class).create(694));

        slot.set(entry);

        assertSame(entry, slot.entry());
    }
}
