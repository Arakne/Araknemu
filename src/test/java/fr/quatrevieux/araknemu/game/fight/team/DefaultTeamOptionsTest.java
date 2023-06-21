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

package fr.quatrevieux.araknemu.game.fight.team;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DefaultTeamOptionsTest extends FightBaseCase {
    private DefaultTeamOptions options;
    private Fight fight;

    @BeforeEach
    @Override
    public void setUp() throws Exception {
        super.setUp();

        fight = createFight();
        options = new DefaultTeamOptions(fight.team(0));
    }

    @Test
    void defaultValues() {
        assertTrue(options.allowSpectators());
        assertTrue(options.allowJoinTeam());
        assertFalse(options.needHelp());
        assertSame(fight.team(0), options.team());

        assertFalse(options.allowSpectatorHasBeenUpdated());
        assertFalse(options.allowJoinTeamHasBeenUpdated());
        assertFalse(options.needHelpHasBeenUpdated());
    }
}
