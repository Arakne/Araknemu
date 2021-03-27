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

package fr.quatrevieux.araknemu.game.fight.builder;

import fr.arakne.utils.value.helper.RandomUtil;
import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.core.event.DefaultListenerAggregate;
import fr.quatrevieux.araknemu.core.event.Dispatcher;
import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.data.world.repository.environment.MapTemplateRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.GameConfiguration;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightHandler;
import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterFactory;
import fr.quatrevieux.araknemu.game.fight.module.FightModule;
import fr.quatrevieux.araknemu.game.fight.state.PlacementState;
import fr.quatrevieux.araknemu.game.fight.type.ChallengeType;
import fr.quatrevieux.araknemu.network.game.out.fight.exploration.AddTeamFighters;
import fr.quatrevieux.araknemu.network.game.out.fight.exploration.FightsCount;
import fr.quatrevieux.araknemu.network.game.out.fight.exploration.HideFight;
import fr.quatrevieux.araknemu.network.game.out.fight.exploration.ShowFight;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertSame;

class FightHandlerTest extends GameBaseCase {
    private FightService service;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps().pushSubAreas().pushAreas();

        service = container.get(FightService.class);
    }

    @Test
    void withChallengeFight() throws ContainerException {
        FightHandler<ChallengeBuilder> handler = new FightHandler<>(
            container.get(FightService.class),
            new ChallengeBuilder(container.get(FightService.class), container.get(FighterFactory.class), new RandomUtil(), container.get(Logger.class), Executors.newSingleThreadScheduledExecutor(), new ChallengeType(configuration.fight()))
        );

        Fight fight = handler.start(
            builder -> {
                try {
                    builder
                        .map(container.get(ExplorationMapService.class).load(10340))
                        .fighter(gamePlayer())
                        .fighter(makeOtherPlayer())
                    ;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        );

        assertInstanceOf(ChallengeType.class, fight.type());
        assertInstanceOf(PlacementState.class, fight.state());
        assertCount(2, fight.teams());
        assertCount(2, fight.fighters());
    }

    @Test
    void displayOnMap() throws SQLException, ContainerException {
        ExplorationMap map = container.get(ExplorationMapService.class).load(10340);

        explorationPlayer().join(map);
        requestStack.clear();

        FightHandler<ChallengeBuilder> handler = new FightHandler<>(
            service,
            new ChallengeBuilder(service, container.get(FighterFactory.class), new RandomUtil(), container.get(Logger.class), Executors.newSingleThreadScheduledExecutor(), new ChallengeType(configuration.fight()))
        );

        Fight fight = handler.start(
            builder -> {
                try {
                    builder
                        .map(map)
                        .fighter(makeSimpleGamePlayer(5))
                        .fighter(makeSimpleGamePlayer(6))
                    ;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        );

        assertSame(fight, service.getFromMap(10340, 1));

        requestStack.assertAll(
            new ShowFight(fight),
            new AddTeamFighters(fight.team(0)),
            new AddTeamFighters(fight.team(1)),
            new FightsCount(1)
        );

        requestStack.clear();

        fight.start();
        requestStack.assertLast(new HideFight(fight));

        fight.stop();
        requestStack.assertLast(new FightsCount(0));

        assertCount(0, service.fightsByMap(10340));
    }

    @Test
    void cancelFightWillRemoveTheFight() throws ContainerException {
        ExplorationMap map = container.get(ExplorationMapService.class).load(10340);

        FightHandler<ChallengeBuilder> handler = new FightHandler<>(
            service,
            new ChallengeBuilder(service, container.get(FighterFactory.class), new RandomUtil(), container.get(Logger.class), Executors.newSingleThreadScheduledExecutor(), new ChallengeType(configuration.fight()))
        );

        Fight fight = handler.start(
            builder -> {
                try {
                    builder
                        .map(map)
                        .fighter(makeSimpleGamePlayer(5))
                        .fighter(makeSimpleGamePlayer(6))
                    ;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        );

        fight.cancel();
        assertCount(0, service.fightsByMap(10340));
    }

    @Test
    void startWillRegisterModules() throws ContainerException {
        DefaultListenerAggregate dispatcher = new DefaultListenerAggregate();
        FightModule module = Mockito.mock(FightModule.class);

        Mockito.when(module.listeners()).thenReturn(new Listener[0]);

        FightHandler<ChallengeBuilder> handler = new FightHandler<>(
            new FightService(
                container.get(MapTemplateRepository.class),
                dispatcher,
                Arrays.asList(
                    new ChallengeBuilderFactory(container.get(FighterFactory.class), new ChallengeType(configuration.fight()), container.get(Logger.class))
                ),
                Arrays.asList(
                    (fight) -> module
                ),
                container.get(GameConfiguration.class).fight()
            ),
            new ChallengeBuilder(service, container.get(FighterFactory.class), new RandomUtil(), container.get(Logger.class), Executors.newSingleThreadScheduledExecutor(), new ChallengeType(configuration.fight()))
        );

        Fight fight = handler.start(
            builder -> {
                try {
                    builder
                        .map(container.get(ExplorationMapService.class).load(10340))
                        .fighter(makeSimpleGamePlayer(5))
                        .fighter(makeSimpleGamePlayer(6))
                    ;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        );

        Mockito.verify(module).listeners();
        Mockito.verify(module).effects(fight.effects());
    }
}
