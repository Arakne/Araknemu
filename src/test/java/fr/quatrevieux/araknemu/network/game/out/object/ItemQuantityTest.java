package fr.quatrevieux.araknemu.network.game.out.object;

import fr.quatrevieux.araknemu.data.living.entity.player.PlayerItem;
import fr.quatrevieux.araknemu.game.player.inventory.InventoryEntry;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ItemQuantityTest {
    @Test
    void generate() {
        assertEquals(
            "OQ5|2",
            new ItemQuantity(
                new InventoryEntry(null, new PlayerItem(1, 5, 0, null, 2, -1), null)
            ).toString()
        );
    }
}