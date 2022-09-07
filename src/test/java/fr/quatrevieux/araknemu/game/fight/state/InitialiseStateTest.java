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

package fr.quatrevieux.araknemu.game.fight.state;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.fight.map.FightMap;
import fr.quatrevieux.araknemu.game.fight.team.SimpleTeam;
import fr.quatrevieux.araknemu.game.fight.turn.action.factory.FightActionsFactoryRegistry;
import fr.quatrevieux.araknemu.game.fight.type.ChallengeType;
import fr.quatrevieux.araknemu.util.ExecutorFactory;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class InitialiseStateTest extends GameBaseCase {
    private Fight fight;
    private FightState nextState;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps().pushSubAreas().pushAreas();

        FightMap map;
        fight = new Fight(
            1,
            new ChallengeType(configuration.fight()),
            map = container.get(FightService.class).map(container.get(ExplorationMapService.class).load(10340)),
            Arrays.asList(
                new SimpleTeam(new PlayerFighter(gamePlayer()), Arrays.asList(map.get(123)), 0),
                new SimpleTeam(new PlayerFighter(makeOtherPlayer()), Arrays.asList(map.get(321)), 1)
            ),
            new StatesFlow(
                new NullState(),
                nextState = new NullState()
            ),
            container.get(Logger.class),
            ExecutorFactory.createSingleThread(),
            container.get(FightActionsFactoryRegistry.class)
        );
    }

    @Test
    void start() {
        new InitialiseState().start(fight);
        assertSame(nextState, fight.state());
    }

    @Test
    void id() {
        assertEquals(1, new InitialiseState().id());
    }
}
