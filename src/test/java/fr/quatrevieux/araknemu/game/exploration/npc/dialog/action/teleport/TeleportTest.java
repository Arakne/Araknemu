package fr.quatrevieux.araknemu.game.exploration.npc.dialog.action.teleport;

import fr.quatrevieux.araknemu.data.world.entity.environment.npc.ResponseAction;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ActionType;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.network.game.out.game.MapData;
import fr.quatrevieux.araknemu.network.game.out.game.action.GameActionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TeleportTest extends GameBaseCase {
    private ExplorationPlayer player;
    private ExplorationMapService service;
    private Teleport.Factory factory;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        service = container.get(ExplorationMapService.class);
        player = explorationPlayer();
        factory = new Teleport.Factory(service);

        requestStack.clear();
    }

    @Test
    void factory() {
        assertInstanceOf(Teleport.class, factory.create(new ResponseAction(1, "", "10340,128")));
    }

    @Test
    void check() {
        assertTrue(factory.create(new ResponseAction(1, "", "10340,128")).check(player));
    }

    @Test
    void apply() {
        factory.create(new ResponseAction(1, "", "10340,128")).apply(player);
        player.interactions().end(1);

        assertEquals(10340, player.map().id());
        assertEquals(128, player.cell().id());

        requestStack.assertAll(
            new GameActionResponse("1", ActionType.CHANGE_MAP, player.id(), ""),
            new MapData(player.map())
        );
    }

    @Test
    void applyWithCinematic() {
        factory.create(new ResponseAction(1, "", "10340,128,5")).apply(player);
        player.interactions().end(1);

        assertEquals(10340, player.map().id());
        assertEquals(128, player.cell().id());

        requestStack.assertAll(
            new GameActionResponse("1", ActionType.CHANGE_MAP, player.id(), "5"),
            new MapData(player.map())
        );
    }
}
