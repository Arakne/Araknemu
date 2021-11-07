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
import fr.quatrevieux.araknemu.core.event.DefaultListenerAggregate;
import fr.quatrevieux.araknemu.core.event.ListenerAggregate;
import fr.quatrevieux.araknemu.data.world.entity.environment.MapTemplate;
import fr.quatrevieux.araknemu.data.world.entity.environment.MapTrigger;
import fr.quatrevieux.araknemu.data.world.repository.environment.MapTemplateRepository;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.area.AreaService;
import fr.quatrevieux.araknemu.game.exploration.event.ExplorationPlayerCreated;
import fr.quatrevieux.araknemu.game.exploration.map.cell.CellLoader;
import fr.quatrevieux.araknemu.game.exploration.map.cell.trigger.TriggerCell;
import fr.quatrevieux.araknemu.game.exploration.map.cell.trigger.action.teleport.Teleport;
import fr.quatrevieux.araknemu.game.exploration.map.event.MapLoaded;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.game.fight.event.FightCreated;
import fr.quatrevieux.araknemu.game.listener.map.*;
import fr.quatrevieux.araknemu.game.listener.map.fight.*;
import fr.quatrevieux.araknemu.game.listener.player.SendMapData;
import fr.quatrevieux.araknemu.network.game.out.fight.exploration.AddTeamFighters;
import fr.quatrevieux.araknemu.network.game.out.fight.exploration.FightsCount;
import fr.quatrevieux.araknemu.network.game.out.fight.exploration.ShowFight;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class ExplorationMapServiceTest extends FightBaseCase {
    private ExplorationMapService service;
    private ListenerAggregate dispatcher;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        service = new ExplorationMapService(
            container.get(MapTemplateRepository.class),
            container.get(FightService.class),
            container.get(AreaService.class),
            dispatcher = new DefaultListenerAggregate(),
            container.get(CellLoader.class)
        );

        dataSet
            .pushMaps()
            .pushAreas()
            .pushSubAreas()
        ;
    }

    @Test
    void load() throws SQLException, ContainerException {
        AtomicReference<MapLoaded> ref = new AtomicReference<>();
        dispatcher.add(MapLoaded.class, ref::set);

        dataSet.pushTrigger(new MapTrigger(10540, 120, Teleport.ACTION_ID, "123,45", ""));

        ExplorationMap map = service.load(10540);

        assertEquals(10540, map.id());
        assertEquals(0, map.sprites().size());
        assertEquals(454, map.subArea().id());
        assertSame(map, ref.get().map());

        ref.set(null);

        assertSame(map, service.load(10540));
        assertNull(ref.get());
        assertNotSame(map, service.load(10300));

        assertTrue(map.dispatcher().has(SendNewSprite.class));
        assertTrue(map.dispatcher().has(SendSpriteRemoved.class));
        assertTrue(map.dispatcher().has(SendPlayerChangeCell.class));
        assertTrue(map.dispatcher().has(SendPlayerChangeOrientation.class));
        assertTrue(map.dispatcher().has(SendCreatureMove.class));
    }

    @Test
    void loadByMapTemplate() throws SQLException, ContainerException {
        MapTemplate template = container.get(MapTemplateRepository.class).get(10540);

        dataSet.pushTrigger(new MapTrigger(10540, 120, Teleport.ACTION_ID, "123,45", ""));

        ExplorationMap map = service.load(template);

        assertEquals(10540, map.id());
        assertEquals(0, map.sprites().size());
        assertEquals(454, map.subArea().id());
        assertSame(map, service.load(template));
    }

    @Test
    void listeners() throws SQLException, ContainerException {
        ListenerAggregate dispatcher = new DefaultListenerAggregate();
        dispatcher.register(service);

        ExplorationPlayer player = new ExplorationPlayer(gamePlayer(true));
        dispatcher.dispatch(new ExplorationPlayerCreated(player));

        assertTrue(player.dispatcher().has(SendMapData.class));
    }

    @Test
    void fightListeners() throws Exception {
        ExplorationMap map = service.load(10340);
        explorationPlayer().changeMap(map, 123);

        ListenerAggregate dispatcher = new DefaultListenerAggregate();
        dispatcher.register(service);

        Fight fight = createSimpleFight(map);
        requestStack.clear();

        dispatcher.dispatch(new FightCreated(fight));

        assertTrue(fight.dispatcher().has(HideFightOnStart.class));
        assertTrue(fight.dispatcher().has(SendFightsCount.class));
        assertTrue(fight.dispatcher().has(SendCancelledFight.class));
        assertTrue(fight.dispatcher().has(SendTeamFighterRemoved.class));
        assertTrue(fight.dispatcher().has(SendTeamFighterAdded.class));

        requestStack.assertAll(
            new ShowFight(fight),
            new AddTeamFighters(fight.team(0)),
            new AddTeamFighters(fight.team(1)),
            new FightsCount(1)
        );
    }

    @Test
    void preloadWithTriggers() throws SQLException, ContainerException {
        dataSet.pushTrigger(new MapTrigger(10300, 120, Teleport.ACTION_ID, "123,45", ""));
        dataSet.pushTrigger(new MapTrigger(10300, 125, Teleport.ACTION_ID, "123,45", ""));
        dataSet.pushTrigger(new MapTrigger(10300, 121, Teleport.ACTION_ID, "123,45", ""));

        assertInstanceOf(TriggerCell.class, service.load(10300).get(120));
        assertInstanceOf(TriggerCell.class, service.load(10300).get(121));
        assertInstanceOf(TriggerCell.class, service.load(10300).get(125));

        Logger logger = Mockito.mock(Logger.class);
        service.preload(logger);

        Mockito.verify(logger).info("Loading maps...");
        Mockito.verify(logger).info(Mockito.eq("{} maps successfully loaded in {}ms"), Mockito.eq(3), Mockito.any());
    }
}
