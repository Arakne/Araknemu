package fr.quatrevieux.araknemu.network.game.out.object;

import fr.quatrevieux.araknemu.data.living.entity.player.PlayerItem;
import fr.quatrevieux.araknemu.game.player.inventory.InventoryEntry;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DestroyItemTest {
    @Test
    void generate() {
        assertEquals(
            "OR3",
            new DestroyItem(
                new InventoryEntry(null, new PlayerItem(1, 3, 1, null, 1, -1), null)
            ).toString()
        );
    }
}
