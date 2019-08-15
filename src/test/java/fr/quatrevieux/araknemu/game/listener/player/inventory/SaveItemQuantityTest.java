package fr.quatrevieux.araknemu.game.listener.player.inventory;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.living.entity.player.PlayerItem;
import fr.quatrevieux.araknemu.data.living.repository.player.PlayerItemRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.item.inventory.event.ObjectQuantityChanged;
import fr.quatrevieux.araknemu.game.player.inventory.InventoryEntry;
import fr.quatrevieux.araknemu.game.item.inventory.ItemEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class SaveItemQuantityTest extends GameBaseCase {
    private SaveItemQuantity listener;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.use(PlayerItem.class);
        listener = new SaveItemQuantity(
            container.get(PlayerItemRepository.class)
        );
    }

    @Test
    void onBadEntry() {
        listener.on(
            new ObjectQuantityChanged(Mockito.mock(ItemEntry.class))
        );
    }

    @Test
    void onObjectQuantityChanged() throws ContainerException {
        InventoryEntry entry = new InventoryEntry(
            null,
            new PlayerItem(1, 1, 284, new ArrayList<>(), 5, -1),
            null
        );

        container.get(PlayerItemRepository.class).add(entry.entity());
        entry.entity().setQuantity(10);

        listener.on(
            new ObjectQuantityChanged(entry)
        );

        assertEquals(10, container.get(PlayerItemRepository.class).get(entry.entity()).quantity());
    }
}