package fr.quatrevieux.araknemu.game.listener.player.inventory;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.living.entity.player.PlayerItem;
import fr.quatrevieux.araknemu.data.living.repository.player.PlayerItemRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.player.inventory.event.ObjectDeleted;
import fr.quatrevieux.araknemu.game.player.inventory.InventoryEntry;
import fr.quatrevieux.araknemu.game.item.inventory.ItemEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class SaveDeletedItemTest extends GameBaseCase {
    private SaveDeletedItem listener;


    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.use(PlayerItem.class);
        listener = new SaveDeletedItem(
            container.get(PlayerItemRepository.class)
        );
    }

    @Test
    void onBadEntry() {
        listener.on(
            new ObjectDeleted(Mockito.mock(ItemEntry.class))
        );
    }

    @Test
    void onObjectDeleted() throws ContainerException {
        InventoryEntry entry = new InventoryEntry(
            null,
            new PlayerItem(1, 1, 284, new ArrayList<>(), 5, -1),
            null
        );

        container.get(PlayerItemRepository.class).add(entry.entity());

        listener.on(
            new ObjectDeleted(entry)
        );

        assertFalse(container.get(PlayerItemRepository.class).has(entry.entity()));
    }
}