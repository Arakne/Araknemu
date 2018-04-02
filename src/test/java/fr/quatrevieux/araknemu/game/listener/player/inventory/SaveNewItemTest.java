package fr.quatrevieux.araknemu.game.listener.player.inventory;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.living.entity.player.PlayerItem;
import fr.quatrevieux.araknemu.data.living.repository.player.PlayerItemRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.player.inventory.event.ObjectAdded;
import fr.quatrevieux.araknemu.game.player.inventory.InventoryEntry;
import fr.quatrevieux.araknemu.game.world.item.inventory.ItemEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class SaveNewItemTest extends GameBaseCase {
    private SaveNewItem listener;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.use(PlayerItem.class);
        listener = new SaveNewItem(
            container.get(PlayerItemRepository.class)
        );
    }

    @Test
    void onBadEntry() {
        listener.on(
            new ObjectAdded(Mockito.mock(ItemEntry.class))
        );
    }

    @Test
    void onObjectAdded() throws ContainerException {
        InventoryEntry entry = new InventoryEntry(
            null,
            new PlayerItem(1, 1, 284, new ArrayList<>(), 5, -1),
            null
        );

        listener.on(
            new ObjectAdded(entry)
        );

        assertTrue(container.get(PlayerItemRepository.class).has(entry.entity()));
    }
}