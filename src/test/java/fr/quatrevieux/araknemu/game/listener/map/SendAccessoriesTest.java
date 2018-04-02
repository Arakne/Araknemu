package fr.quatrevieux.araknemu.game.listener.map;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.living.entity.player.PlayerItem;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.player.inventory.event.EquipmentChanged;
import fr.quatrevieux.araknemu.game.player.inventory.InventoryEntry;
import fr.quatrevieux.araknemu.network.game.out.object.SpriteAccessories;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

class SendAccessoriesTest extends GameBaseCase {
    private SendAccessories listener;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        listener = new SendAccessories(
            explorationPlayer()
        );

        requestStack.clear();
    }

    @Test
    void onEquipmentChangedNotAccessory() {
        listener.on(
            new EquipmentChanged(
                new InventoryEntry(null, new PlayerItem(1, 1, 1, null, 1, 0), null),
                -1, true
            )
        );

        requestStack.assertEmpty();
    }

    @Test
    void onEquipmentChangedWithAccessoryOnEntry() throws SQLException, ContainerException {
        listener.on(
            new EquipmentChanged(
                new InventoryEntry(null, new PlayerItem(1, 1, 1, null, 1, 1), null),
                -1, true
            )
        );

        requestStack.assertLast(
            new SpriteAccessories(
                gamePlayer().id(),
                gamePlayer().inventory().accessories()
            )
        );
    }

    @Test
    void onEquipmentChangedWithAccessoryOnSlot() throws SQLException, ContainerException {
        listener.on(
            new EquipmentChanged(
                new InventoryEntry(null, new PlayerItem(1, 1, 1, null, 1, -1), null),
                1, true
            )
        );

        requestStack.assertLast(
            new SpriteAccessories(
                gamePlayer().id(),
                gamePlayer().inventory().accessories()
            )
        );
    }
}
