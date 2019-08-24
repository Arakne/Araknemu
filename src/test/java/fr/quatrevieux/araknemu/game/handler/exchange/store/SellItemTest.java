package fr.quatrevieux.araknemu.game.handler.exchange.store;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.exchange.ExchangeFactory;
import fr.quatrevieux.araknemu.game.exploration.exchange.ExchangeType;
import fr.quatrevieux.araknemu.game.exploration.npc.NpcService;
import fr.quatrevieux.araknemu.game.item.ItemService;
import fr.quatrevieux.araknemu.game.item.inventory.ItemEntry;
import fr.quatrevieux.araknemu.network.exception.CloseImmediately;
import fr.quatrevieux.araknemu.network.game.in.exchange.store.SellRequest;
import fr.quatrevieux.araknemu.network.game.out.exchange.store.ItemSold;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SellItemTest extends GameBaseCase {
    private ExplorationPlayer player;
    private ItemService service;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushNpcWithStore();

        player = explorationPlayer();
        service = container.get(ItemService.class);
    }

    @Test
    void success() throws Exception {
        openStore();

        ItemEntry entry = player.inventory().add(service.create(39), 12);

        handlePacket(new SellRequest(entry.id(), 5));

        requestStack.assertLast(ItemSold.success());
        assertEquals(15275, player.inventory().kamas());
        assertEquals(7, entry.quantity());
    }

    @Test
    void failedItemNotAvailable() throws Exception {
        openStore();

        handlePacket(new SellRequest(404, 5));

        requestStack.assertLast(ItemSold.failed());
    }

    @Test
    void failedNotInExploration() {
        session.setExploration(null);
        assertThrows(CloseImmediately.class, () -> handlePacket(new SellRequest(39, 5)));
    }

    private void openStore() {
        player.interactions().start(container.get(ExchangeFactory.class).create(
            ExchangeType.NPC_STORE,
            player,
            container.get(NpcService.class).get(10001)
        ));
    }
}