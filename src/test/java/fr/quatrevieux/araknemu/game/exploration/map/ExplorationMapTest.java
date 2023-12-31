/*
 * This file is part of Araknemu.
 *
 * Araknemu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Araknemu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Araknemu.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Copyright (c) 2017-2019 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.exploration.map;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.data.value.Geolocation;
import fr.quatrevieux.araknemu.data.world.entity.environment.MapTemplate;
import fr.quatrevieux.araknemu.data.world.entity.environment.MapTrigger;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.creature.ExplorationCreature;
import fr.quatrevieux.araknemu.game.exploration.creature.Operation;
import fr.quatrevieux.araknemu.game.exploration.map.cell.BasicCell;
import fr.quatrevieux.araknemu.game.exploration.map.cell.CellLoader;
import fr.quatrevieux.araknemu.game.exploration.map.cell.CellLoaderAggregate;
import fr.quatrevieux.araknemu.game.exploration.map.cell.trigger.MapTriggerService;
import fr.quatrevieux.araknemu.game.exploration.map.cell.trigger.TriggerCell;
import fr.quatrevieux.araknemu.game.exploration.map.cell.trigger.TriggerLoader;
import fr.quatrevieux.araknemu.game.exploration.map.event.NewSpriteOnMap;
import fr.quatrevieux.araknemu.game.exploration.map.event.SpriteRemoveFromMap;
import fr.quatrevieux.araknemu.game.exploration.npc.GameNpc;
import fr.quatrevieux.araknemu.game.exploration.npc.NpcService;
import fr.quatrevieux.araknemu.network.game.out.game.RemoveSprite;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExplorationMapTest extends GameBaseCase {
    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps().pushSubAreas().pushAreas();
    }

    @Test
    void data() throws ContainerException {
        MapTemplate template = dataSet.refresh(new MapTemplate(10300, null, null, null, null, null, null, 0, false));

        ExplorationMap map = new ExplorationMap(template, new CellLoaderAggregate(new CellLoader[0]), null);

        assertEquals(10300, map.id());
        assertEquals(template.date(), map.date());
        assertEquals(template.key(), map.key());
        assertEquals(new Geolocation(-4, 3), map.geolocation());
    }

    @Test
    void addPlayerWillAddSprite() throws ContainerException, SQLException {
        MapTemplate template = dataSet.refresh(new MapTemplate(10300, null, null, null, null, null, null, 0, false));

        ExplorationMap map = new ExplorationMap(template, new CellLoaderAggregate(new CellLoader[0]), null);

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

        MapTemplate template = dataSet.refresh(new MapTemplate(10300, null, null, null, null, null, null, 0, false));

        ExplorationMap map = new ExplorationMap(template, new CellLoaderAggregate(new CellLoader[0]), null);
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
        MapTemplate template = dataSet.refresh(new MapTemplate(10300, null, null, null, null, null, null, 0, false));
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

        ExplorationMap map = new ExplorationMap(template, new CellLoaderAggregate(new CellLoader[0]), null);
        map.dispatcher().add(listener);

        assertEquals(0, map.sprites().size());
        assertEquals(0, map.creatures().size());

        GameNpc npc = container.get(NpcService.class).get(457);

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

        AtomicReference<NewSpriteOnMap> ref = new AtomicReference<>();

        map.add(player);

        map.dispatcher().add(NewSpriteOnMap.class, ref::set);
        assertThrows(IllegalArgumentException.class, () -> map.add(player));
        assertNull(ref.get());
    }

    @Test
    void removeNotExists() throws Exception {
        ExplorationMap map = explorationPlayer().map();
        ExplorationPlayer player = makeOtherExplorationPlayer();

        AtomicReference<SpriteRemoveFromMap> ref = new AtomicReference<>();
        map.dispatcher().add(SpriteRemoveFromMap.class, ref::set);

        assertFalse(map.remove(player));
        assertNull(ref.get());
    }

    @Test
    void sendWillSendToPlayers() throws ContainerException, SQLException {
        MapTemplate template = dataSet.refresh(new MapTemplate(10300, null, null, null, null, null, null, 0, false));

        ExplorationMap map = new ExplorationMap(template, new CellLoaderAggregate(new CellLoader[0]), null);

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

        AtomicReference<SpriteRemoveFromMap> ref = new AtomicReference<>();
        map.dispatcher().add(SpriteRemoveFromMap.class, ref::set);

        other.changeMap(map, 123);
        assertTrue(map.remove(other));
        assertEquals(other.sprite(), ref.get().sprite());

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
    void fightPlaces() {
        ExplorationMap map = container.get(ExplorationMapService.class).load(10340);

        assertTrue(map.fightPlaces(4).isEmpty());
        assertCollectionEquals(
            map.fightPlaces(1),
            map.get(48), map.get(63), map.get(75), map.get(90), map.get(92), map.get(106), map.get(121), map.get(122), map.get(137), map.get(150)
        );
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
        MapTemplate template = dataSet.refresh(new MapTemplate(10300, null, null, null, null, null, null, 0, false));
        dataSet.pushTrigger(new MapTrigger(10300, 123, 0, "10300,465", "-1"));
        dataSet.pushTrigger(new MapTrigger(10300, 125, 0, "10340,365", "-1"));

        ExplorationMap map = new ExplorationMap(template, new CellLoaderAggregate(new CellLoader[] {
            new TriggerLoader(container.get(MapTriggerService.class))
        }), null);

        assertInstanceOf(TriggerCell.class, map.get(123));
        assertInstanceOf(TriggerCell.class, map.get(125));

        assertSame(map.get(123), map.get(123));
        assertSame(map.get(125), map.get(125));
    }

    @Test
    void getBasicCellWillBeReinstantiated() throws ContainerException, SQLException {
        MapTemplate template = dataSet.refresh(new MapTemplate(10300, null, null, null, null, null, null, 0, false));

        ExplorationMap map = new ExplorationMap(template, new CellLoaderAggregate(new CellLoader[0]), null);

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

        GameNpc npc = container.get(NpcService.class).get(457);

        map.add(npc);
        map.add(other);

        Collection<GameNpc> npcs = new ArrayList<>();
        Collection<ExplorationPlayer> players = new ArrayList<>();

        assertNull(map.apply(new Operation<Void>() {
            @Override
            public Void onExplorationPlayer(ExplorationPlayer player) {
                players.add(player);
                return null;
            }

            @Override
            public Void onNpc(GameNpc npc) {
                npcs.add(npc);
                return null;
            }
        }));

        assertEquals(Arrays.asList(explorationPlayer(), other), players);
        assertEquals(Arrays.asList(npc), npcs);
    }

    @Test
    void applyWithReturnValueShouldIgnoreNextCreatures() throws Exception {
        dataSet.pushNpcs();
        ExplorationMap map = explorationPlayer().map();
        ExplorationPlayer other = makeOtherExplorationPlayer();

        GameNpc npc = container.get(NpcService.class).get(457);

        map.add(npc);
        map.add(other);

        Collection<ExplorationCreature> creatures = new ArrayList<>();

        assertSame(explorationPlayer(), map.apply(new Operation<ExplorationCreature>() {
            @Override
            public ExplorationCreature onCreature(ExplorationCreature creature) {
                creatures.add(creature);
                return creature;
            }
        }));

        assertEquals(Arrays.asList(explorationPlayer()), creatures);
    }
}
