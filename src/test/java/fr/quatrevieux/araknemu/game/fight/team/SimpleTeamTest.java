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

package fr.quatrevieux.araknemu.game.fight.team;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.constant.Alignment;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.JoinFightError;
import fr.quatrevieux.araknemu.game.fight.exception.JoinFightException;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.operation.FighterOperation;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class SimpleTeamTest extends FightBaseCase {
    private SimpleTeam team;
    private PlayerFighter fighter;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        team = new SimpleTeam(
            fighter = new PlayerFighter(gamePlayer(true)),
            Arrays.asList(123, 456),
            1
        );

        team.setFight(createFight());
    }

    @Test
    void withoutFight() throws SQLException {
        team = new SimpleTeam(
            fighter = new PlayerFighter(gamePlayer(true)),
            Arrays.asList(123, 456),
            1
        );

        assertSame(fighter, team.leader());
        assertEquals(Arrays.asList(fighter), new ArrayList<>(team.fighters())); // Make list copy for equality
        assertEquals(Arrays.asList(123, 456), team.startPlaces());
        assertEquals(1, team.number());
        assertEquals(1, team.id());
        assertEquals(0, team.type());
        assertEquals(Alignment.NONE, team.alignment());
        assertEquals(player.position().cell(), team.cell());

        assertSame(team, fighter.team());

        assertThrows(IllegalStateException.class, team::options);
    }

    @Test
    void getters() throws Exception {
        assertSame(fighter, team.leader());
        assertEquals(Arrays.asList(fighter), new ArrayList<>(team.fighters())); // Make list copy for equality
        assertEquals(Arrays.asList(123, 456), team.startPlaces());
        assertEquals(1, team.number());
        assertEquals(1, team.id());
        assertEquals(0, team.type());
        assertEquals(Alignment.NONE, team.alignment());
        assertEquals(player.position().cell(), team.cell());

        assertSame(team, fighter.team());
        assertInstanceOf(ConfigurableTeamOptions.class, team.options());
        assertTrue(team.options().allowJoinTeam());
        assertTrue(team.options().allowSpectators());
        assertFalse(team.options().needHelp());
    }

    @Test
    void send() {
        team.send("test");

        requestStack.assertLast("test");
    }

    @Test
    void alive() throws Exception {
        assertTrue(team.alive());

        Fight fight = createFight();
        fighter.joinFight(fight, fight.map().get(123));
        fighter.init();
        fighter.life().alter(fighter, -1000);

        assertFalse(team.alive());
    }

    @Test
    void joinNotPlayerFighter() {
        Fighter fighter = Mockito.mock(Fighter.class);

        Mockito.when(fighter.apply(Mockito.any()))
            .then(invocation -> {
                invocation.<FighterOperation>getArgument(0).onGenericFighter(fighter);

                return null;
            })
        ;

        assertThrows(JoinFightException.class, () -> team.join(fighter));
    }

    @Test
    void joinFullTeam() throws SQLException, ContainerException, JoinFightException {
        team.join(new PlayerFighter(makeSimpleGamePlayer(10)));

        try {
            team.join(new PlayerFighter(makeSimpleGamePlayer(11)));

            fail("JoinFightException expected");
        } catch (JoinFightException e) {
            assertEquals(JoinFightError.TEAM_FULL, e.error());
        }
    }

    @Test
    void joinSuccess() throws SQLException, ContainerException, JoinFightException {
        PlayerFighter fighter = new PlayerFighter(makeSimpleGamePlayer(10));
        team.join(fighter);

        assertCount(2, team.fighters());
        assertContains(fighter, team.fighters());
        assertSame(team, fighter.team());
    }

    @Test
    void joinLocked() throws Exception {
        team.setFight(createFight());
        team.options().toggleAllowJoinTeam();
        PlayerFighter fighter = new PlayerFighter(makeSimpleGamePlayer(10));

        try {
            team.join(fighter);

            fail("JoinFightException expected");
        } catch (JoinFightException e) {
            assertEquals(JoinFightError.TEAM_CLOSED, e.error());
        }
    }

    @Test
    void kick() throws SQLException, ContainerException, JoinFightException {
        PlayerFighter fighter = new PlayerFighter(makeSimpleGamePlayer(10));
        team.join(fighter);

        team.kick(fighter);

        assertFalse(team.fighters().contains(fighter));
        assertCount(1, team.fighters());
    }
}
