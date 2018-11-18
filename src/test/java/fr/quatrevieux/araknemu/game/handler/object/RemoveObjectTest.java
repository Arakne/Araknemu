package fr.quatrevieux.araknemu.game.handler.object;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.item.ItemService;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.player.inventory.InventoryEntry;
import fr.quatrevieux.araknemu.network.exception.ErrorPacket;
import fr.quatrevieux.araknemu.network.game.in.object.ObjectDeleteRequest;
import fr.quatrevieux.araknemu.network.game.out.info.Error;
import fr.quatrevieux.araknemu.network.game.out.object.DestroyItem;
import fr.quatrevieux.araknemu.network.game.out.object.ItemDeletionError;
import fr.quatrevieux.araknemu.network.game.out.object.ItemQuantity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class RemoveObjectTest extends FightBaseCase {
    private RemoveObject handler;
    private InventoryEntry entry;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        GamePlayer player = gamePlayer(true);
        dataSet.pushItemTemplates();

        entry = player.inventory().add(container.get(ItemService.class).create(40), 10);
        requestStack.clear();

        handler = new RemoveObject();
    }

    @Test
    void handleError() throws Exception {
        try {
            handler.handle(session, new ObjectDeleteRequest(45, 10));

            fail("ErrorPacket should be thrown");
        } catch (ErrorPacket e) {
            assertInstanceOf(ItemDeletionError.class, e.packet());
        }
    }

    @Test
    void handleSuccessAllItems() throws Exception {
        handler.handle(session, new ObjectDeleteRequest(entry.id(), 10));

        requestStack.assertLast(
            new DestroyItem(entry)
        );
    }

    @Test
    void handleSuccessPartial() throws Exception {
        handler.handle(session, new ObjectDeleteRequest(entry.id(), 3));

        assertEquals(7, entry.quantity());

        requestStack.assertLast(
            new ItemQuantity(entry)
        );
    }

    @Test
    void functionalNotAllowedOnActiveFight() throws Exception {
        Fight fight = createFight();
        fight.start();

        assertErrorPacket(Error.cantDoDuringFight(), () -> handlePacket(new ObjectDeleteRequest(entry.id(), 10)));
    }

    @Test
    void functionalSuccessDuringPlacement() throws Exception {
        createFight();

        handlePacket(new ObjectDeleteRequest(entry.id(), 10));

        requestStack.assertLast(
            new DestroyItem(entry)
        );
    }
}
