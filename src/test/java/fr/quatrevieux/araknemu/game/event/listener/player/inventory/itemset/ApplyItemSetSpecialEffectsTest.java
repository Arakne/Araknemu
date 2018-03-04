package fr.quatrevieux.araknemu.game.event.listener.player.inventory.itemset;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.event.inventory.EquipmentChanged;
import fr.quatrevieux.araknemu.game.item.ItemService;
import fr.quatrevieux.araknemu.game.player.characteristic.SpecialEffects;
import fr.quatrevieux.araknemu.game.player.inventory.InventoryEntry;
import fr.quatrevieux.araknemu.game.player.inventory.slot.BeltSlot;
import fr.quatrevieux.araknemu.game.player.inventory.slot.BootsSlot;
import fr.quatrevieux.araknemu.game.world.item.inventory.exception.InventoryException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class ApplyItemSetSpecialEffectsTest extends GameBaseCase {
    private ApplyItemSetSpecialEffects listener;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        listener = new ApplyItemSetSpecialEffects(
            gamePlayer(true)
        );

        dataSet
            .pushItemTemplates()
            .pushItemSets()
        ;
    }

    @Test
    void onEquipmentAdded() throws SQLException, ContainerException, InventoryException {
        gamePlayer().inventory().add(container.get(ItemService.class).create(8213), 1, 0);
        gamePlayer().inventory().add(container.get(ItemService.class).create(8219), 1, 2);
        gamePlayer().inventory().add(container.get(ItemService.class).create(8225), 1, BootsSlot.SLOT_ID);
        gamePlayer().inventory().add(container.get(ItemService.class).create(8231), 1, 7);
        InventoryEntry entry = gamePlayer().inventory().add(container.get(ItemService.class).create(8243), 1, 6);

        gamePlayer().characteristics().specials().clear();

        listener.on(new EquipmentChanged(entry, 6, true));

        assertEquals(30, gamePlayer().characteristics().specials().get(SpecialEffects.Type.INITIATIVE));
    }

    @Test
    void onEquipmentRemoved() throws SQLException, ContainerException, InventoryException {
        gamePlayer().inventory().add(container.get(ItemService.class).create(8213), 1, 0);
        gamePlayer().inventory().add(container.get(ItemService.class).create(8219), 1, 2);
        gamePlayer().inventory().add(container.get(ItemService.class).create(8225), 1, BootsSlot.SLOT_ID);
        gamePlayer().inventory().add(container.get(ItemService.class).create(8231), 1, 7);
        InventoryEntry entry = gamePlayer().inventory().add(container.get(ItemService.class).create(8243), 1, -1);

        gamePlayer().characteristics().specials().add(SpecialEffects.Type.INITIATIVE, 30);

        listener.on(new EquipmentChanged(entry, 6, false));

        assertEquals(0, gamePlayer().characteristics().specials().get(SpecialEffects.Type.INITIATIVE));
    }
}