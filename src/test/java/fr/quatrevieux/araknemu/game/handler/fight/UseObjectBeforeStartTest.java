package fr.quatrevieux.araknemu.game.handler.fight;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.item.ItemService;
import fr.quatrevieux.araknemu.game.player.inventory.InventoryEntry;
import fr.quatrevieux.araknemu.network.game.in.object.ObjectUseRequest;
import fr.quatrevieux.araknemu.network.game.out.account.Stats;
import fr.quatrevieux.araknemu.network.game.out.basic.Noop;
import fr.quatrevieux.araknemu.network.game.out.info.Error;
import fr.quatrevieux.araknemu.network.game.out.info.Information;
import fr.quatrevieux.araknemu.network.game.out.object.DestroyItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UseObjectBeforeStartTest extends FightBaseCase {
    private UseObjectBeforeStart handler;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        handler = new UseObjectBeforeStart();

        dataSet.pushUsableItems();
        requestStack.clear();
    }

    @Test
    void handleDoNothing() throws Exception {
        createFight();

        InventoryEntry entry = player.inventory().add(container.get(ItemService.class).create(468));
        requestStack.clear();

        handler.handle(session, new ObjectUseRequest(entry.id(), 0, 0, false));

        requestStack.assertAll(new Noop());
        assertEquals(1, entry.quantity());    }

    @Test
    void handleSuccess() throws Exception {
        player.life().set(10);

        createFight();

        InventoryEntry entry = player.inventory().add(container.get(ItemService.class).create(468));
        requestStack.clear();

        handler.handle(session, new ObjectUseRequest(entry.id(), 0, 0, false));

        requestStack.assertAll(
            new Stats(player),
            Information.heal(10),
            new DestroyItem(entry)
        );

        assertEquals(20, player.life().current());
        assertEquals(20, player.fighter().life().current());
        assertEquals(0, entry.quantity());
    }

    @Test
    void functionalErrorOnActiveFight() throws Exception {
        InventoryEntry entry = player.inventory().add(container.get(ItemService.class).create(468));

        Fight fight = createFight();
        fight.start();

        assertErrorPacket(Error.cantDoDuringFight(), () -> handlePacket(new ObjectUseRequest(entry.id(), 0, 0, false)));
    }

    @Test
    void functionalSuccess() throws Exception {
        player.life().set(10);

        InventoryEntry entry = player.inventory().add(container.get(ItemService.class).create(468));

        createFight();

        handlePacket(new ObjectUseRequest(entry.id(), 0, 0, false));
        assertEquals(20, player.fighter().life().current());
    }
}
