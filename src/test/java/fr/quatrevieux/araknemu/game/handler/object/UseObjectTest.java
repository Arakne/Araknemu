package fr.quatrevieux.araknemu.game.handler.object;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ActionType;
import fr.quatrevieux.araknemu.game.item.ItemService;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.player.inventory.InventoryEntry;
import fr.quatrevieux.araknemu.game.item.inventory.exception.ItemNotFoundException;
import fr.quatrevieux.araknemu.network.game.in.object.ObjectUseRequest;
import fr.quatrevieux.araknemu.network.game.out.account.Stats;
import fr.quatrevieux.araknemu.network.game.out.basic.Noop;
import fr.quatrevieux.araknemu.network.game.out.game.action.GameActionResponse;
import fr.quatrevieux.araknemu.network.game.out.info.Information;
import fr.quatrevieux.araknemu.network.game.out.object.DestroyItem;
import fr.quatrevieux.araknemu.network.game.out.object.ItemQuantity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UseObjectTest extends GameBaseCase {
    private UseObject handler;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        handler = new UseObject();

        dataSet.pushUsableItems();
        gamePlayer(true);

        explorationPlayer();
        requestStack.clear();
    }

    @Test
    void handleForSelfSuccessWithBoostStatsObject() throws Exception {
        InventoryEntry entry = explorationPlayer().inventory().add(container.get(ItemService.class).create(800));
        requestStack.clear();

        handler.handle(session, new ObjectUseRequest(entry.id(), 0, 0, false));

        requestStack.assertAll(
            new Stats(gamePlayer()),
            Information.characteristicBoosted(Characteristic.AGILITY, 1),
            new DestroyItem(entry)
        );

        assertThrows(ItemNotFoundException.class, () -> explorationPlayer().inventory().get(entry.id()));
        assertEquals(0, entry.quantity());
        assertEquals(1, explorationPlayer().characteristics().base().get(Characteristic.AGILITY));
    }

    @Test
    void handleDoNothing() throws Exception {
        InventoryEntry entry = explorationPlayer().inventory().add(container.get(ItemService.class).create(468));
        requestStack.clear();

        handler.handle(session, new ObjectUseRequest(entry.id(), 0, 0, false));

        requestStack.assertAll(new Noop());
    }

    @Test
    void handleForTargetPlayer() throws Exception {
        InventoryEntry entry = explorationPlayer().inventory().add(container.get(ItemService.class).create(468));

        GamePlayer other = makeOtherPlayer();
        ExplorationPlayer otherPlayer = new ExplorationPlayer(other);
        explorationPlayer().map().add(otherPlayer);
        other.life().set(10);
        requestStack.clear();

        handler.handle(session, new ObjectUseRequest(entry.id(), otherPlayer.id(), 0, true));

        requestStack.assertAll(new DestroyItem(entry));
        assertEquals(20, other.life().current());
        assertEquals(0, entry.quantity());
    }

    @Test
    void handleForTargetCell() throws Exception {
        InventoryEntry entry = explorationPlayer().inventory().add(container.get(ItemService.class).create(2240), 100);
        requestStack.clear();

        handler.handle(session, new ObjectUseRequest(entry.id(), 0, 150, true));

        requestStack.assertAll(
            new GameActionResponse(1, ActionType.FIREWORK, explorationPlayer().id(), "150,2900,11,8,1"),
            new ItemQuantity(entry)
        );
        assertEquals(99, entry.quantity());
    }
}
