package fr.quatrevieux.araknemu.game.exploration.map;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.data.constant.Sex;
import fr.quatrevieux.araknemu.data.value.Colors;
import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.data.world.entity.environment.MapTemplate;
import fr.quatrevieux.araknemu.data.world.entity.environment.MapTrigger;
import fr.quatrevieux.araknemu.data.world.entity.environment.npc.Npc;
import fr.quatrevieux.araknemu.data.world.entity.environment.npc.NpcTemplate;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.map.cell.BasicCell;
import fr.quatrevieux.araknemu.game.exploration.map.cell.CellLoader;
import fr.quatrevieux.araknemu.game.exploration.map.cell.CellLoaderAggregate;
import fr.quatrevieux.araknemu.game.exploration.map.cell.trigger.MapTriggerService;
import fr.quatrevieux.araknemu.game.exploration.map.cell.trigger.TriggerCell;
import fr.quatrevieux.araknemu.game.exploration.map.cell.trigger.TriggerLoader;
import fr.quatrevieux.araknemu.game.exploration.map.event.NewSpriteOnMap;
import fr.quatrevieux.araknemu.game.exploration.npc.GameNpc;
import fr.quatrevieux.araknemu.game.listener.map.*;
import fr.quatrevieux.araknemu.game.world.creature.Creature;
import fr.quatrevieux.araknemu.game.world.creature.Operation;
import fr.quatrevieux.araknemu.game.world.map.Direction;
import fr.quatrevieux.araknemu.network.game.out.game.RemoveSprite;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class ExplorationMapTest extends GameBaseCase {
    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps();
    }

    @Test
    void data() throws ContainerException {
        MapTemplate template = dataSet.refresh(new MapTemplate(10300, null, null, null, null, null));

        ExplorationMap map = new ExplorationMap(template, new CellLoaderAggregate(new CellLoader[0]));

        assertEquals(10300, map.id());
        assertEquals(template.date(), map.date());
        assertEquals(template.key(), map.key());
    }

    @Test
    void addPlayerWillAddSprite() throws ContainerException, SQLException {
        MapTemplate template = dataSet.refresh(new MapTemplate(10300, null, null, null, null, null));

        ExplorationMap map = new ExplorationMap(template, new CellLoaderAggregate(new CellLoader[0]));

        assertEquals(0, map.sprites().size());

        map.add(explorationPlayer());

        assertEquals(1, map.sprites().size());
        assertEquals(explorationPlayer().sprite().toString(), map.sprites().toArray()[0].toString());
    }

    @Test
    void addPlayerWillDispatchEvent() throws Exception {
        AtomicReference<NewSpriteOnMap> ref = new AtomicReference<>();

        Listener<NewSpriteOnMap> listener = new Listener<NewSpriteOnMap>() {
            @Override
            public void on(NewSpriteOnMap event) {
                ref.set(event);
            }

            @Override
            public Class<NewSpriteOnMap> event() {
                return NewSpriteOnMap.class;
            }
        };

        ExplorationPlayer player = explorationPlayer();

        MapTemplate template = dataSet.refresh(new MapTemplate(10300, null, null, null, null, null));

        ExplorationMap map = new ExplorationMap(template, new CellLoaderAggregate(new CellLoader[0]));
        map.dispatcher().add(listener);

        map.add(player);

        assertNotNull(ref.get());
        assertEquals(player.sprite().toString(), ref.get().sprite().toString());

        ExplorationPlayer other = new ExplorationPlayer(makeOtherPlayer());

        map.add(other);

        assertEquals(other.sprite().toString(), ref.get().sprite().toString());
    }

    @Test
    void addNpc() throws ContainerException, SQLException {
        MapTemplate template = dataSet.refresh(new MapTemplate(10300, null, null, null, null, null));
        dataSet.pushNpcs();

        AtomicReference<NewSpriteOnMap> ref = new AtomicReference<>();

        Listener<NewSpriteOnMap> listener = new Listener<NewSpriteOnMap>() {
            @Override
            public void on(NewSpriteOnMap event) {
                ref.set(event);
            }

            @Override
            public Class<NewSpriteOnMap> event() {
                return NewSpriteOnMap.class;
            }
        };

        ExplorationMap map = new ExplorationMap(template, new CellLoaderAggregate(new CellLoader[0]));
        map.dispatcher().add(listener);

        assertEquals(0, map.sprites().size());
        assertEquals(0, map.creatures().size());

        GameNpc npc = new GameNpc(
            dataSet.refresh(new Npc(457, 0, null, null)),
            dataSet.refresh(new NpcTemplate(848, 0, 0, 0, null, null, null, 0, 0))
        );

        map.add(npc);

        assertEquals(1, map.sprites().size());
        assertEquals(1, map.creatures().size());
        assertEquals(npc.sprite().toString(), map.sprites().toArray()[0].toString());
        assertSame(npc, map.creature(-45704));
        assertCollectionEquals(map.creatures(), npc);
        assertSame(npc.sprite(), ref.get().sprite());
    }

    @Test
    void addAlreadyAdded() throws Exception {
        ExplorationMap map = explorationPlayer().map();
        ExplorationPlayer player = makeOtherExplorationPlayer();

        map.add(player);
        assertThrows(IllegalArgumentException.class, () -> map.add(player));
    }

    @Test
    void removeNotExists() throws Exception {
        ExplorationMap map = explorationPlayer().map();
        ExplorationPlayer player = makeOtherExplorationPlayer();

        assertThrows(IllegalArgumentException.class, () -> map.remove(player));
    }

    @Test
    void sendWillSendToPlayers() throws ContainerException, SQLException {
        MapTemplate template = dataSet.refresh(new MapTemplate(10300, null, null, null, null, null));

        ExplorationMap map = new ExplorationMap(template, new CellLoaderAggregate(new CellLoader[0]));

        ExplorationPlayer player = explorationPlayer();
        player.join(map);

        map.send("my packet");

        requestStack.assertLast("my packet");
    }

    @Test
    void removeWillSendPacket() throws Exception {
        explorationPlayer();
        ExplorationPlayer other = new ExplorationPlayer(makeOtherPlayer());

        ExplorationMap map = explorationPlayer().map();

        other.join(map);
        map.remove(other);

        requestStack.assertLast(
            new RemoveSprite(other.sprite())
        );

        assertTrue(map.creatures().contains(explorationPlayer()));
        assertFalse(map.creatures().contains(other));
    }

    @Test
    void creatureNotFound() throws SQLException, ContainerException {
        ExplorationMap map = explorationPlayer().map();

        assertThrows(NoSuchElementException.class, () -> map.creature(-5));
    }

    @Test
    void creatureFound() throws SQLException, ContainerException {
        ExplorationMap map = explorationPlayer().map();

        assertSame(explorationPlayer(), map.creature(explorationPlayer().id()));
    }

    @Test
    void has() throws SQLException, ContainerException {
        ExplorationMap map = explorationPlayer().map();

        assertTrue(map.has(explorationPlayer().id()));
        assertFalse(map.has(-5));
    }

    @Test
    void canLaunchFight() throws ContainerException {
        assertFalse(container.get(ExplorationMapService.class).load(10300).canLaunchFight());
        assertTrue(container.get(ExplorationMapService.class).load(10340).canLaunchFight());
    }

    @Test
    void cellEquals() throws SQLException, ContainerException {
        ExplorationMap map = explorationPlayer().map();

        assertEquals(map.get(123), map.get(123));
        assertNotEquals(map.get(123), map.get(124));
        assertNotEquals(map.get(123), new Object());
    }

    @Test
    void getPreloadedCellsWillKeepInstance() throws ContainerException, SQLException {
        MapTemplate template = dataSet.refresh(new MapTemplate(10300, null, null, null, null, null));
        dataSet.pushTrigger(new MapTrigger(10300, 123, 0, "10300,465", "-1"));
        dataSet.pushTrigger(new MapTrigger(10300, 125, 0, "10340,365", "-1"));

        ExplorationMap map = new ExplorationMap(template, new CellLoaderAggregate(new CellLoader[] {
            new TriggerLoader(container.get(MapTriggerService.class))
        }));

        assertInstanceOf(TriggerCell.class, map.get(123));
        assertInstanceOf(TriggerCell.class, map.get(125));

        assertSame(map.get(123), map.get(123));
        assertSame(map.get(125), map.get(125));
    }

    @Test
    void getBasicCellWillBeReinstantiated() throws ContainerException, SQLException {
        MapTemplate template = dataSet.refresh(new MapTemplate(10300, null, null, null, null, null));

        ExplorationMap map = new ExplorationMap(template, new CellLoaderAggregate(new CellLoader[0]));

        assertInstanceOf(BasicCell.class, map.get(456));
        assertNotSame(map.get(456), map.get(456));
        assertEquals(map.get(456), map.get(456));
        assertEquals(456, map.get(456).id());
        assertSame(map, map.get(456).map());
    }

    @Test
    void apply() throws Exception {
        dataSet.pushNpcs();
        ExplorationMap map = explorationPlayer().map();
        ExplorationPlayer other = makeOtherExplorationPlayer();

        GameNpc npc = new GameNpc(
            dataSet.refresh(new Npc(457, 0, null, null)),
            dataSet.refresh(new NpcTemplate(848, 0, 0, 0, null, null, null, 0, 0))
        );

        map.add(npc);
        map.add(other);

        Collection<GameNpc> npcs = new ArrayList<>();
        Collection<ExplorationPlayer> players = new ArrayList<>();

        map.apply(new Operation() {
            @Override
            public void onExplorationPlayer(ExplorationPlayer player) {
                players.add(player);
            }

            @Override
            public void onNpc(GameNpc npc) {
                npcs.add(npc);
            }
        });

        assertEquals(Arrays.asList(explorationPlayer(), other), players);
        assertEquals(Arrays.asList(npc), npcs);
    }
}
