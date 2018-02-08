package fr.quatrevieux.araknemu.game.handler.object;

import fr.quatrevieux.araknemu.data.living.entity.player.PlayerItem;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.item.ItemService;
import fr.quatrevieux.araknemu.network.game.in.object.ObjectMoveRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @todo test errors
 */
class MoveObjectTest extends GameBaseCase {
    private MoveObject handler;

    private ItemService itemService;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        handler = new MoveObject();
        itemService = container.get(ItemService.class);

        gamePlayer();

        dataSet.pushItemTemplates();
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
}
