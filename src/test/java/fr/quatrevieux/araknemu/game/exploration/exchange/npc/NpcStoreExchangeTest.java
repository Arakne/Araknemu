package fr.quatrevieux.araknemu.game.exploration.exchange.npc;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.exchange.npc.NpcStoreExchange;
import fr.quatrevieux.araknemu.game.exploration.interaction.exchange.npc.StoreDialog;
import fr.quatrevieux.araknemu.game.exploration.npc.GameNpc;
import fr.quatrevieux.araknemu.game.exploration.npc.NpcService;
import fr.quatrevieux.araknemu.game.item.ItemService;
import fr.quatrevieux.araknemu.game.item.inventory.ItemEntry;
import fr.quatrevieux.araknemu.game.item.inventory.exception.InventoryException;
import fr.quatrevieux.araknemu.network.game.out.exchange.store.NpcStoreList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NpcStoreExchangeTest extends GameBaseCase {
    private NpcStoreExchange exchange;
    private GameNpc npc;
    private ExplorationPlayer player;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushNpcWithStore();

        player = explorationPlayer();
        npc = container.get(NpcService.class).get(10001);
        exchange = new NpcStoreExchange(player, npc, npc.store());
    }

    @Test
    void send() {
        exchange.send("test");

        requestStack.assertLast("test");
    }

    @Test
    void start() {
        exchange.start();

        requestStack.assertLast(new NpcStoreList(npc.store().available()));
    }

    @Test
    void dialog() {
        assertInstanceOf(StoreDialog.class, exchange.dialog());
    }

    @Test
    void stop() {
        player.interactions().start(exchange.dialog());
        exchange.stop();

        assertFalse(player.interactions().busy());
    }

    @Test
    void buySuccess() {
        exchange.buy(39, 2);

        assertEquals(15025, player.inventory().kamas());
        assertEquals(39, player.inventory().get(1).templateId());
        assertEquals(2, player.inventory().get(1).quantity());
    }

    @Test
    void buyInvalidQuantity() {
        assertThrows(IllegalArgumentException.class, () -> exchange.buy(39, -5));

        assertEquals(15225, player.inventory().kamas());
        assertEquals(0, player.inventory().stream().count());
    }

    @Test
    void buyInvalidItem() {
        assertThrows(IllegalArgumentException.class, () -> exchange.buy(404, 1));

        assertEquals(15225, player.inventory().kamas());
        assertEquals(0, player.inventory().stream().count());
    }

    @Test
    void buyNotEnoughKamas() {
        assertThrows(IllegalArgumentException.class, () -> exchange.buy(39, 1000000));

        assertEquals(15225, player.inventory().kamas());
        assertEquals(0, player.inventory().stream().count());
    }

    @Test
    void sellItemNotFound() {
        assertThrows(InventoryException.class, () -> exchange.sell(404, 5));
        assertEquals(15225, player.inventory().kamas());
    }

    @Test
    void sellTooMany() {
        ItemEntry entry = player.inventory().add(container.get(ItemService.class).create(39), 3);

        assertThrows(InventoryException.class, () -> exchange.sell(entry.id(), 5));
        assertEquals(15225, player.inventory().kamas());
        assertEquals(3, entry.quantity());
    }

    @Test
    void sellInvalidQuantity() {
        ItemEntry entry = player.inventory().add(container.get(ItemService.class).create(39), 3);

        assertThrows(InventoryException.class, () -> exchange.sell(entry.id(), -5));
        assertEquals(15225, player.inventory().kamas());
        assertEquals(3, entry.quantity());
    }

    @Test
    void sellSuccess() {
        ItemEntry entry = player.inventory().add(container.get(ItemService.class).create(39), 3);

        exchange.sell(entry.id(), 2);
        assertEquals(15245, player.inventory().kamas());
        assertEquals(1, entry.quantity());
    }

    @Test
    void sellSuccessWithoutKamas() {
        ItemEntry entry = player.inventory().add(container.get(ItemService.class).create(8213), 100);

        exchange.sell(entry.id(), 100);
        assertEquals(15225, player.inventory().kamas());
        assertEquals(0, entry.quantity());
    }
}
