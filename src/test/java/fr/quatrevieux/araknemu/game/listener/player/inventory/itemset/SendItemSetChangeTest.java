package fr.quatrevieux.araknemu.game.listener.player.inventory.itemset;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.player.inventory.event.EquipmentChanged;
import fr.quatrevieux.araknemu.game.item.ItemService;
import fr.quatrevieux.araknemu.game.player.inventory.InventoryEntry;
import fr.quatrevieux.araknemu.game.item.Item;
import fr.quatrevieux.araknemu.network.game.out.object.UpdateItemSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

class SendItemSetChangeTest extends GameBaseCase {
    private SendItemSetChange listener;
    private ItemService service;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushItemSets().pushItemTemplates();

        service = container.get(ItemService.class);
        listener = new SendItemSetChange(gamePlayer(true));
        requestStack.clear();
    }

    @Test
    void onEquipmentChangedWithItemSet() throws SQLException, ContainerException {
        Item item = service.create(2425);

        listener.on(
            new EquipmentChanged(
                new InventoryEntry(null, null, item),
                0, true
            )
        );

        requestStack.assertLast(
            new UpdateItemSet(gamePlayer().inventory().itemSets().get(item.set().get()))
        );
    }

    @Test
    void onEquipmentChangedWithoutItemSet() throws SQLException, ContainerException {
        Item item = service.create(39);

        listener.on(
            new EquipmentChanged(
                new InventoryEntry(null, null, item),
                0, true
            )
        );

        requestStack.assertEmpty();
    }
}
