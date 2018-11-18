package fr.quatrevieux.araknemu.game.handler.object;

import fr.quatrevieux.araknemu.data.living.entity.player.PlayerItem;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.item.ItemService;
import fr.quatrevieux.araknemu.game.player.inventory.InventoryEntry;
import fr.quatrevieux.araknemu.game.player.inventory.slot.MantleSlot;
import fr.quatrevieux.araknemu.network.game.in.object.ObjectMoveRequest;
import fr.quatrevieux.araknemu.network.game.out.info.Error;
import fr.quatrevieux.araknemu.network.game.out.object.AddItemError;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 *
 */
class MoveObjectTest extends FightBaseCase {
    private MoveObject handler;

    private ItemService itemService;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        handler = new MoveObject();
        itemService = container.get(ItemService.class);

        gamePlayer();

        dataSet
            .pushItemTemplates()
            .pushItemSets()
        ;
        dataSet.use(PlayerItem.class);

        gamePlayer().inventory().add(
            itemService.create(39),
            1,
            0
        );

        gamePlayer().inventory().add(
            itemService.create(40),
            1, 1
        );

        gamePlayer().inventory().add(
            itemService.create(2425, true),
            10, -1
        );

        requestStack.clear();
    }

    @Test
    void handleSuccess() throws Exception {
        handler.handle(session, new ObjectMoveRequest(1, -1, 1));

        assertEquals(-1, gamePlayer().inventory().get(1).position());
        assertEquals(1, gamePlayer().inventory().get(1).quantity());
    }

    @Test
    void handleErrorTooLowLevel() throws Exception {
        dataSet.pushHighLevelItems();

        InventoryEntry entry = gamePlayer().inventory().add(itemService.create(112414));

        handler.handle(session, new ObjectMoveRequest(entry.id(), MantleSlot.SLOT_ID, 1));

        requestStack.assertLast(
            new AddItemError(AddItemError.Error.TOO_LOW_LEVEL)
        );
    }

    @Test
    void functionalNotAllowedOnActiveFight() throws Exception {
        Fight fight = createFight();
        fight.start();

        assertErrorPacket(Error.cantDoDuringFight(), () -> handlePacket(new ObjectMoveRequest(1, -1, 1)));
    }

    @Test
    void functionalSuccessDuringPlacement() throws Exception {
        createFight();

        handlePacket(new ObjectMoveRequest(1, -1, 1));

        assertEquals(-1, gamePlayer().inventory().get(1).position());
        assertEquals(1, gamePlayer().inventory().get(1).quantity());
    }
}
