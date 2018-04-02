package fr.quatrevieux.araknemu.game.exploration.interaction.action;

import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.exploration.map.trigger.Teleport;
import fr.quatrevieux.araknemu.network.game.out.game.MapData;
import fr.quatrevieux.araknemu.network.game.out.game.action.GameActionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.helpers.NOPLogger;

import static org.junit.jupiter.api.Assertions.*;

class ChangeMapTest extends GameBaseCase {
    private ExplorationPlayer player;
    private ExplorationMap map;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps();

        player = explorationPlayer();
        map = container.get(ExplorationMapService.class).load(10540);
    }

    @Test
    void dataNoCinematic() throws Exception {
        ChangeMap changeMap = new ChangeMap(player, map, 123);
        changeMap.setId(2);

        assertEquals(2, changeMap.id());
        assertSame(player, changeMap.performer());
        assertEquals(ActionType.CHANGE_MAP, changeMap.type());
        assertArrayEquals(new Object[] {""}, changeMap.arguments());
    }

    @Test
    void dataCinematic() throws Exception {
        ChangeMap changeMap = new ChangeMap(player, map, 123, 6);
        changeMap.setId(2);

        assertEquals(2, changeMap.id());
        assertSame(player, changeMap.performer());
        assertEquals(ActionType.CHANGE_MAP, changeMap.type());
        assertArrayEquals(new Object[] {6}, changeMap.arguments());
    }

    @Test
    void startWithoutCinematic() throws Exception {
        ExplorationMap lastMap = player.map();

        ChangeMap changeMap = new ChangeMap(player, map, 123);
        changeMap.setId(2);

        changeMap.start();

        assertFalse(lastMap.players().contains(player));
        requestStack.assertLast(
            new GameActionResponse(changeMap)
        );
    }

    @Test
    void startWithCinematic() throws Exception {
        ExplorationMap lastMap = player.map();

        ChangeMap changeMap = new ChangeMap(player, map, 123, 5);
        changeMap.setId(2);

        changeMap.start();

        assertFalse(lastMap.players().contains(player));
        requestStack.assertLast(
            new GameActionResponse(2, ActionType.CHANGE_MAP, player.id(), "5")
        );
    }

    @Test
    void startWillNotChangePosition() throws Exception {
        ExplorationMap lastMap = player.map();
        Position lastPosition = player.position();

        ChangeMap changeMap = new ChangeMap(player, map, 123, 5);
        changeMap.setId(2);

        changeMap.start();

        assertSame(lastMap, player.map());
        assertEquals(lastPosition, player.position());
    }

    @Test
    void end() throws Exception {
        ChangeMap changeMap = new ChangeMap(player, map, 123, 5);
        changeMap.end();

        assertSame(map, player.map());
        assertEquals(new Position(10540, 123), player.position());

        requestStack.assertLast(
            new MapData(map)
        );
    }
}
