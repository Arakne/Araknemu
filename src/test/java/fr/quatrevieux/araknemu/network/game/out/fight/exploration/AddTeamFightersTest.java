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

package fr.quatrevieux.araknemu.network.game.out.fight.exploration;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.exception.JoinFightException;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.fight.map.FightMap;
import fr.quatrevieux.araknemu.game.fight.team.FightTeam;
import fr.quatrevieux.araknemu.game.fight.team.SimpleTeam;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 */
class AddTeamFightersTest extends FightBaseCase {
    @Test
    void generate() throws SQLException, ContainerException {
        FightTeam team = new SimpleTeam(makePlayerFighter(gamePlayer(true)), new ArrayList<>(), 1);

        assertEquals("Gt1|+1;Bob;50", new AddTeamFighters(team).toString());
    }

    @Test
    void generateWithMultipleFighters() throws SQLException, ContainerException, JoinFightException {
        FightMap map = loadFightMap(10340);
        FightTeam team = new SimpleTeam(makePlayerFighter(gamePlayer(true)), Arrays.asList(map.get(123), map.get(456)), 1);
        team.join(makePlayerFighter(makeSimpleGamePlayer(10)));

        assertEquals("Gt1|+1;Bob;50|+10;PLAYER_10;1", new AddTeamFighters(team).toString());
    }
}
