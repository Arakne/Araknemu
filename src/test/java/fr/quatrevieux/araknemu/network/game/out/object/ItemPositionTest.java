package fr.quatrevieux.araknemu.network.game.out.object;

import fr.quatrevieux.araknemu.data.living.entity.player.PlayerItem;
import fr.quatrevieux.araknemu.game.player.inventory.InventoryEntry;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ItemPositionTest {
    @Test
    void generate() {
        assertEquals(
            "OM5|2",
            new ItemPosition(
                new InventoryEntry(null, new PlayerItem(1, 5, 0, null, 1, 2), null)
            ).toString()
        );
    }
}
