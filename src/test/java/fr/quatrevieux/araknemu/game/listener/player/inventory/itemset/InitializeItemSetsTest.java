package fr.quatrevieux.araknemu.game.listener.player.inventory.itemset;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.player.event.GameJoined;
import fr.quatrevieux.araknemu.game.item.ItemService;
import fr.quatrevieux.araknemu.game.player.inventory.InventoryEntry;
import fr.quatrevieux.araknemu.game.item.inventory.exception.InventoryException;
import fr.quatrevieux.araknemu.network.game.out.object.UpdateItemSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

class InitializeItemSetsTest extends GameBaseCase {
    private InitializeItemSets listener;
    private ItemService service;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushItemSets().pushItemTemplates();

        service = container.get(ItemService.class);
        listener = new InitializeItemSets(gamePlayer(true));

        requestStack.clear();
    }

    @Test
    void onGameJoinedWithoutItemSets() {
        listener.on(new GameJoined());

        requestStack.assertEmpty();
    }

    @Test
    void onGameJoinedWithItemSets() throws SQLException, ContainerException, InventoryException {
        InventoryEntry entry1 = gamePlayer().inventory().add(service.create(2425), 1, 0);
        InventoryEntry entry2 = gamePlayer().inventory().add(service.create(2641), 1, 6);

        requestStack.clear();

        listener.on(new GameJoined());

        requestStack.assertAll(
            new UpdateItemSet(gamePlayer().inventory().itemSets().get(entry1.item().set().get())),
            new UpdateItemSet(gamePlayer().inventory().itemSets().get(entry2.item().set().get()))
        );
    }
}
