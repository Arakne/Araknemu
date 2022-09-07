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

package fr.quatrevieux.araknemu.game.fight;

import fr.arakne.utils.value.helper.RandomUtil;
import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.core.event.DefaultListenerAggregate;
import fr.quatrevieux.araknemu.core.event.ListenerAggregate;
import fr.quatrevieux.araknemu.data.value.Geolocation;
import fr.quatrevieux.araknemu.game.GameConfiguration;
import fr.quatrevieux.araknemu.game.GameService;
import fr.quatrevieux.araknemu.game.event.GameStopped;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.event.ExplorationPlayerCreated;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.fight.builder.BaseBuilder;
import fr.quatrevieux.araknemu.game.fight.builder.ChallengeBuilder;
import fr.quatrevieux.araknemu.game.fight.builder.ChallengeBuilderFactory;
import fr.quatrevieux.araknemu.game.fight.event.FightCreated;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterFactory;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.fight.module.FightModule;
import fr.quatrevieux.araknemu.game.fight.module.RaulebaqueModule;
import fr.quatrevieux.araknemu.game.fight.state.StatesFlow;
import fr.quatrevieux.araknemu.game.fight.team.SimpleTeam;
import fr.quatrevieux.araknemu.game.fight.turn.action.factory.FightActionsFactoryRegistry;
import fr.quatrevieux.araknemu.game.fight.type.ChallengeType;
import fr.quatrevieux.araknemu.game.listener.player.exploration.LeaveExplorationForFight;
import fr.quatrevieux.araknemu.game.listener.player.fight.AttachFighter;
import fr.quatrevieux.araknemu.game.player.event.PlayerLoaded;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FightServiceTest extends FightBaseCase {
    private FightService service;
    private ListenerAggregate dispatcher;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps().pushSubAreas().pushAreas();

        service = new FightService(
            dispatcher = new DefaultListenerAggregate(),
            Arrays.asList(
                new ChallengeBuilderFactory(container.get(FighterFactory.class), container.get(ChallengeType.class))
            ),
            Arrays.asList(
                RaulebaqueModule::new
            ),
            container.get(FightService.FightFactory.class),
            container.get(GameConfiguration.class).fight()
        );
    }

    @Test
    void map() throws ContainerException {
        assertNotNull(service.map(container.get(ExplorationMapService.class).load(10340)));
    }

    @Test
    void handler() {
        assertNotNull(service.handler(ChallengeBuilder.class));
        assertThrows(NoSuchElementException.class, () -> service.handler(new fr.quatrevieux.araknemu.game.fight.builder.FightBuilder() {
            @Override
            public Fight build(int fightId) {
                return null;
            }
        }.getClass()));
    }

    @Test
    void playerLoadedListener() throws SQLException, ContainerException {
        ListenerAggregate dispatcher = new DefaultListenerAggregate();
        dispatcher.register(service);

        dispatcher.dispatch(new PlayerLoaded(gamePlayer()));

        assertTrue(gamePlayer().dispatcher().has(AttachFighter.class));
    }

    @Test
    void explorationPlayerCreatedListener() throws SQLException, ContainerException {
        ListenerAggregate dispatcher = new DefaultListenerAggregate();
        dispatcher.register(service);

        ExplorationPlayer player = new ExplorationPlayer(gamePlayer(true));
        dispatcher.dispatch(new ExplorationPlayerCreated(player));

        assertTrue(player.dispatcher().has(LeaveExplorationForFight.class));
    }

    @Test
    void gameStoppedListenerShouldCancelFight() throws Exception {
        ListenerAggregate dispatcher = new DefaultListenerAggregate();
        dispatcher.register(service);

        Fight fight = createFight(true);
        service.created(fight);

        dispatcher.dispatch(new GameStopped(container.get(GameService.class)));

        assertTrue(service.fightsByMap(fight.map().id()).isEmpty());
        assertCount(0, fight.teams());
        assertCount(0, fight.fighters());
    }

    @Test
    void newFightId() {
        assertEquals(1, service.newFightId());
        assertEquals(2, service.newFightId());
    }

    @Test
    void created() throws Exception {
        Fight fight = createFight(false);

        AtomicReference<FightCreated> ref = new AtomicReference<>();
        dispatcher.add(FightCreated.class, ref::set);

        service.created(fight);

        assertSame(fight, ref.get().fight());
        assertSame(fight, service.getFromMap(10340, 1));
        assertCollectionEquals(service.fightsByMap(10340), fight);
    }

    @Test
    void remove() throws Exception {
        Fight fight = createFight(false);

        service.remove(fight); // No-op (not yet on map)

        service.created(fight);
        service.remove(fight);

        assertCount(0, service.fightsByMap(10340));
    }

    @Test
    void getFromMapInvalidMap() {
        assertThrows(NoSuchElementException.class, () -> service.getFromMap(0, 0));
    }

    @Test
    void getFromMapInvalidFightId() throws Exception {
        Fight fight = createFight(false);

        service.created(fight);

        assertThrows(NoSuchElementException.class, () -> service.getFromMap(10340, 0));
    }

    @Test
    void fightsByMapNoFights() {
        assertCount(0, service.fightsByMap(10340));
    }

    @Test
    void fightsByMap() throws ContainerException, SQLException {
        ExplorationMap map = container.get(ExplorationMapService.class).load(10340);

        Fight fight1 = createSimpleFight(map);
        Fight fight2 = createSimpleFight(map);
        Fight fight3 = createSimpleFight(map);

        service.created(fight1);
        service.created(fight2);
        service.created(fight3);

        assertCollectionEquals(service.fightsByMap(10340), fight1, fight2, fight3);
    }

    @Test
    void fights() throws ContainerException, SQLException {
        ExplorationMap map1 = container.get(ExplorationMapService.class).load(10340);
        dataSet.pushMap(1, "0706131721", 15, 17, "682a5a717d49457e73274e3b3023452652224870524b735e6260457e377a4136216f7b5a7b332c55426c7b2776207136333f384333676577377828273860497a36214973525b606b6d3e7c4173716a713c6b232477664f3a6d2f79664f325f655b503e3a6f2c34202330272c4824635349657c2d554a31466a3f7e78667e485d527a203f37495d27664b5333207268452f2532426b74447e3a41215a386a6a5b70223f2d3078335a204543292d496c6366287637525723743f3e4c7155726e262f5f48703b294d4b537b544a4b3f4f7150512670323b6b43295a2e762129393254423944752e74636a6671693a235d34253235677a765841", "HhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaa6GHhaaeaaaaaHhaaeaaaaaHhaae6HaaaHhaae60aaaHhaaeaaaaaHhaae6HaaaHhaaeaaaaaGhaaeaaa7oHhaae6HiaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhaaeaaa6SHhgSe6HaaaHhaaeaaa6IHhGaeaaaaaHhGaeaaaaaHhqaeaaaqgHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaGhaaeaaa7iHhGaeaaaaaHhGaeaaa6IHhMSeaaaaaHhaaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhWaeaaaaaHhGaeJgaaaHhGaeaaaaaGhaaeaaa7hHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaa6THhGaeaaaaaHhGaeaaaaaHhMSe62aaaHhGaeaaaaaHhGaeaaaaaHhGaeaaa6IHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhqaeaaaqgGhaaeaaa7AHhGaeaaaaaHhGaeaaaaaHhaae6Ha7eHhGaeaaaaaHhGaeaaaaaHhGaeaaa6IHhWaeaaaaaHhGaeaaaaaGhaaeaaa7gHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeJgaaaHhGaeaaaaaGhaaeaaa7jHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhWaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGae8uaaaGhaaeaaa7jHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGae8uaaaHhWae60aaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhqaeaaaqgHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeJgaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaGhaaeaaa7iHhGaeJgaaaHhaaeaaaaaHhaaeJgaaaHhGae6HaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhWaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaa6IHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaa6IGhaaeaaa7hGhaaeaaa7iHhGaeaaaaaHhGaeaaaaaHhGaeJgaaaHhGaeaaaaaHhGaeaaaaaGhaaeaaa7lGhaae8sa7gHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhGaeaaaaaGhaaeaaa7gGhaaeaaa7kHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhWae62aaaGhaaeaaa7kGhaaeaaa7hHhGaeaaaaaHhGaeaaaaaGhaaeaaa7lHhaaeaaaaaGhaaeaaa7nHhGaeaaaaaGhaaeaaa7lGhaaeaaa7jHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaGhaaeaaa7hHhGaeaaaaaGhaaeaaa7mHhGaeaaaaaGhaaeJga7hHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhMTgJgaaaHhGaeaaaaaHhGaeaaa6IHhGaeaaaaaHhGaeaaaaaHhGae8saaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhMSeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaae6HaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaGhaaeaaa7jHhGaeaaa6IHhGaeaaaaaHhaaeaaaaaHhaaeaaa6IHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaGhaaeaaa7gHhGaeaaaaaHhGaeaaaaaHhaaeaaa6GHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaGhaaeaaa7kHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaa6GHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhgTeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaa7dHhaaeaaaaaHhaaeaaa6WHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaGhaaeaaa7yHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhaaeaaa6XHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhMVgaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaa", "a3btbYb_cacQcRc3c5dg|aWa_blbAbCbQb5b6cjcw", new Geolocation(3, 6), 449, false);
        ExplorationMap map2 = container.get(ExplorationMapService.class).load(1);

        Fight fight1 = createSimpleFight(map1);
        Fight fight2 = createSimpleFight(map1);
        Fight fight3 = createSimpleFight(map2);

        service.created(fight1);
        service.created(fight2);
        service.created(fight3);

        assertCollectionEquals(service.fights(), fight1, fight2, fight3);
    }

    @Test
    void modules() throws Exception {
        PlayerFighter fighter1 = makePlayerFighter(gamePlayer());
        PlayerFighter fighter2 = makePlayerFighter(other);

        BaseBuilder builder = new BaseBuilder(service, new RandomUtil(), new ChallengeType(configuration.fight()));
        builder.map(container.get(ExplorationMapService.class).load(10340));
        builder.addTeam((number, startPlaces) -> new SimpleTeam(fighter1, startPlaces, number));
        builder.addTeam((number, startPlaces) -> new SimpleTeam(fighter2, startPlaces, number));
        Fight fight = builder.build(1);

        Collection<FightModule> modules = service.modules(fight);

        assertCount(1, modules);
        assertContainsType(RaulebaqueModule.class, modules);
    }

    @Test
    void create() {
        Fight fight = service.create(1, new ChallengeType(configuration.fight()), loadFightMap(10340), Collections.emptyList(), new StatesFlow());

        assertEquals(1, fight.id());
        assertInstanceOf(ChallengeType.class, fight.type());
        assertEquals(10340, fight.map().id());
        assertEquals(container.get(FightActionsFactoryRegistry.class), fight.actions());
    }
}
