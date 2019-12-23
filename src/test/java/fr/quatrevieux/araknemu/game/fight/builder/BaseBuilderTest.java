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

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.fight.team.FightTeam;
import fr.quatrevieux.araknemu.game.fight.team.SimpleTeam;
import fr.quatrevieux.araknemu.game.fight.type.ChallengeType;
import fr.quatrevieux.araknemu.game.fight.type.FightType;
import fr.quatrevieux.araknemu.util.RandomUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.helpers.NOPLogger;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class BaseBuilderTest extends GameBaseCase {
    private BaseBuilder builder;
    private FightType type;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps().pushSubAreas().pushAreas();

        type = Mockito.mock(FightType.class);
        builder = new BaseBuilder(container.get(FightService.class), new RandomUtil(), type, NOPLogger.NOP_LOGGER);
    }

    @Test
    void buildSimple() throws Exception {
        PlayerFighter fighter = new PlayerFighter(gamePlayer());
        PlayerFighter other = new PlayerFighter(makeOtherPlayer());

        builder.addTeam((number, startPlaces) -> new SimpleTeam(fighter, startPlaces, number));
        builder.addTeam((number, startPlaces) -> new SimpleTeam(other, startPlaces, number));
        builder.map(container.get(ExplorationMapService.class).load(10340));

        Fight fight = builder.build(1);

        assertSame(type, fight.type());
        assertCount(2, fight.teams());
        assertCount(2, fight.fighters(false));
        assertContainsOnly(SimpleTeam.class, fight.teams());
        assertContainsOnly(PlayerFighter.class, fight.fighters());
        assertEquals(1, fight.id());
    }

    @Test
    void buildEmpty() {
        builder.map(container.get(ExplorationMapService.class).load(10340));

        Fight fight = builder.build(1);

        assertSame(type, fight.type());
        assertCount(0, fight.teams());
        assertCount(0, fight.fighters(false));
        assertEquals(1, fight.id());
    }

    @Test
    void buildWillRandomizeTeamNumbers() throws Exception {
        PlayerFighter fighter = new PlayerFighter(gamePlayer());
        PlayerFighter other = new PlayerFighter(makeOtherPlayer());

        builder.addTeam((number, startPlaces) -> new SimpleTeam(fighter, startPlaces, number));
        builder.addTeam((number, startPlaces) -> new SimpleTeam(other, startPlaces, number));
        builder.map(container.get(ExplorationMapService.class).load(10340));

        int nbTeam0 = 0;

        for (int i = 0; i < 100; ++i) {
            Fight fight = builder.build(1);

            Fighter firstFighter = new ArrayList<>(fight.team(0).fighters()).get(0);

            if (firstFighter.id() == gamePlayer().id()) {
                ++nbTeam0;
            }
        }

        assertNotEquals(0, nbTeam0);
        assertNotEquals(100, nbTeam0);
    }

    @Test
    void buildWithoutRandomizeTeam() throws Exception {
        builder = new BaseBuilder(container.get(FightService.class), null, type, NOPLogger.NOP_LOGGER);

        PlayerFighter fighter = new PlayerFighter(gamePlayer());
        PlayerFighter other = new PlayerFighter(makeOtherPlayer());

        builder.addTeam((number, startPlaces) -> new SimpleTeam(fighter, startPlaces, number));
        builder.addTeam((number, startPlaces) -> new SimpleTeam(other, startPlaces, number));
        builder.map(container.get(ExplorationMapService.class).load(10340));

        for (int i = 0; i < 100; ++i) {
            Fight fight = builder.build(1);

            Fighter firstFighter = new ArrayList<>(fight.team(0).fighters()).get(0);

            assertEquals(gamePlayer().id(), firstFighter.id());
        }
    }
}
