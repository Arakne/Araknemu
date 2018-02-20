package fr.quatrevieux.araknemu.game.event.listener.player.inventory;

import fr.quatrevieux.araknemu.data.living.entity.player.PlayerItem;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.event.inventory.ObjectDeleted;
import fr.quatrevieux.araknemu.game.player.inventory.InventoryEntry;
import fr.quatrevieux.araknemu.network.game.out.object.DestroyItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SendItemDeletedTest extends GameBaseCase {
    private SendItemDeleted listener;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        listener = new SendItemDeleted(gamePlayer(true));

        requestStack.clear();
    }

    @Test
    void onObjectDeleted() {
        InventoryEntry entry = new InventoryEntry(null, new PlayerItem(1, 5, 3, null, 1, 3), null);

        listener.on(
            new ObjectDeleted(entry)
        );

        requestStack.assertLast(
            new DestroyItem(entry)
        );
    }
}
