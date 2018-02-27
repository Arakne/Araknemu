package fr.quatrevieux.araknemu.game.player.inventory.slot;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.living.entity.player.PlayerItem;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.event.DefaultListenerAggregate;
import fr.quatrevieux.araknemu.game.item.ItemService;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.player.inventory.InventoryEntry;
import fr.quatrevieux.araknemu.game.world.item.Item;
import fr.quatrevieux.araknemu.game.item.effect.ItemEffect;
import fr.quatrevieux.araknemu.game.world.item.inventory.ItemStorage;
import fr.quatrevieux.araknemu.game.world.item.inventory.SimpleItemStorage;
import fr.quatrevieux.araknemu.game.world.item.inventory.exception.InventoryException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class DefaultSlotTest extends GameBaseCase {
    private DefaultSlot slot;
    private ItemStorage<InventoryEntry> storage;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushItemTemplates();

        GamePlayer player = gamePlayer();

        slot = new DefaultSlot(
            storage = new SimpleItemStorage<>(
                new DefaultListenerAggregate(),
                (id, item, quantity, position) -> new InventoryEntry(
                    player.inventory(),
                    new PlayerItem(1, id, item.template().id(), item.effects().stream().map(ItemEffect::toTemplate).collect(Collectors.toList()), quantity, position),
                    item
                )
            )
        );
    }

    @Test
    void getters() {
        assertNull(slot.entry());
        assertEquals(-1, slot.id());
        slot.check(null, 1);
    }

    @Test
    void setNewEntry() throws ContainerException, InventoryException {
        InventoryEntry entry = slot.set(
            container.get(ItemService.class).create(39),
            10
        );

        assertEquals(39, entry.templateId());
        assertEquals(10, entry.quantity());
        assertEquals(-1, entry.position());

        assertSame(
            entry,
            storage.get(entry.id())
        );
    }

    @Test
    void setAfterMoveWillStackItem() throws ContainerException, InventoryException, SQLException {
        Item item = container.get(ItemService.class).create(39);
        InventoryEntry entry = slot.set(item, 3);

        InventoryEntry newEntry = slot.set(
            new InventoryEntry(
                gamePlayer().inventory(),
                new PlayerItem(1, 1, 39, null, 4, -1),
                item
            )
        );

        assertSame(entry, newEntry);
        assertEquals(39, entry.templateId());
        assertEquals(7, entry.quantity());
        assertEquals(-1, entry.position());
    }
}
