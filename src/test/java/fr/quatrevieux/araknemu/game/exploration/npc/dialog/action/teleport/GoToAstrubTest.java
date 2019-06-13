package fr.quatrevieux.araknemu.game.exploration.npc.dialog.action.teleport;

import fr.quatrevieux.araknemu.data.world.entity.environment.npc.ResponseAction;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ActionType;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.network.game.out.game.MapData;
import fr.quatrevieux.araknemu.network.game.out.game.action.GameActionResponse;
import fr.quatrevieux.araknemu.network.game.out.info.Information;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GoToAstrubTest extends GameBaseCase {
    private ExplorationPlayer player;
    private ExplorationMapService service;
    private GoToAstrub.Factory factory;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        service = container.get(ExplorationMapService.class);
        player = explorationPlayer();
        factory = new GoToAstrub.Factory(service);

        requestStack.clear();
    }

    @Test
    void factory() {
        assertInstanceOf(GoToAstrub.class, factory.create(new ResponseAction(1, "", "")));
    }

    @Test
    void check() {
        assertTrue(factory.create(new ResponseAction(1, "", "")).check(player));
    }

    @Test
    void apply() {
        factory.create(new ResponseAction(1, "", "")).apply(player);
        player.interactions().end(1);

        assertEquals(player.player().race().astrubPosition(), player.position());
        assertEquals(player.player().race().astrubPosition(), player.player().savedPosition());

        assertEquals(10340, player.map().id());
        assertEquals(250, player.cell().id());

        requestStack.assertAll(
            new GameActionResponse("1", ActionType.CHANGE_MAP, player.id(), "7"),
            Information.positionSaved(),
            new MapData(player.map())
        );
    }
}
