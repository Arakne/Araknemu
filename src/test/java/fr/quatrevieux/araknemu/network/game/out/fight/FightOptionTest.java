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
 * Copyright (c) 2017-2021 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.network.game.out.fight;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.team.ConfigurableTeamOptions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FightOptionTest extends FightBaseCase {
    @Test
    void string() {
        assertEquals("Go+H123", new FightOption(123, FightOption.Type.NEED_HELP, true).toString());
        assertEquals("Go-H123", new FightOption(123, FightOption.Type.NEED_HELP, false).toString());
        assertEquals("Go+S123", new FightOption(123, FightOption.Type.BLOCK_SPECTATOR, true).toString());
    }

    @Test
    void blockSpectators() throws Exception {
        Fight fight = createFight();
        ConfigurableTeamOptions options = (ConfigurableTeamOptions) fight.team(0).options();

        assertEquals("Go-S1", FightOption.blockSpectators(options).toString());

        options.toggleAllowSpectators();
        assertEquals("Go+S1", FightOption.blockSpectators(options).toString());
    }

    @Test
    void blockJoiner() throws Exception {
        Fight fight = createFight();
        ConfigurableTeamOptions options = (ConfigurableTeamOptions) fight.team(0).options();

        assertEquals("Go-A1", FightOption.blockJoiner(options).toString());

        options.toggleAllowJoinTeam();
        assertEquals("Go+A1", FightOption.blockJoiner(options).toString());
    }

    @Test
    void needHelp() throws Exception {
        Fight fight = createFight();
        ConfigurableTeamOptions options = (ConfigurableTeamOptions) fight.team(0).options();

        assertEquals("Go-H1", FightOption.needHelp(options).toString());

        options.toggleNeedHelp();
        assertEquals("Go+H1", FightOption.needHelp(options).toString());
    }
}
