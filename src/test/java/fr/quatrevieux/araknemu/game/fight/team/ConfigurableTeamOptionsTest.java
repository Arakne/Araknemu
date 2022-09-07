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
import fr.quatrevieux.araknemu.game.fight.team.event.AllowJoinTeamChanged;
import fr.quatrevieux.araknemu.game.fight.team.event.AllowSpectatorChanged;
import fr.quatrevieux.araknemu.game.fight.team.event.NeedHelpChanged;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class ConfigurableTeamOptionsTest extends FightBaseCase {
    private ConfigurableTeamOptions options;
    private Fight fight;

    @BeforeEach
    @Override
    public void setUp() throws Exception {
        super.setUp();

        fight = createFight();
        options = new ConfigurableTeamOptions(fight.team(0), fight);
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

    @Test
    void toggleAllowSpectators() {
        AtomicReference<AllowSpectatorChanged> ref = new AtomicReference<>();
        fight.dispatcher().add(AllowSpectatorChanged.class, ref::set);

        options.toggleAllowSpectators();

        assertFalse(options.allowSpectators());
        assertTrue(options.allowSpectatorHasBeenUpdated());
        assertFalse(ref.get().spectatorsAllowed());
        assertSame(options, ref.get().options());

        options.toggleAllowSpectators();

        assertTrue(options.allowSpectators());
        assertFalse(options.allowSpectatorHasBeenUpdated());
        assertTrue(ref.get().spectatorsAllowed());
        assertSame(options, ref.get().options());
    }

    @Test
    void toggleAllowJoinTeam() {
        AtomicReference<AllowJoinTeamChanged> ref = new AtomicReference<>();
        fight.dispatcher().add(AllowJoinTeamChanged.class, ref::set);

        options.toggleAllowJoinTeam();

        assertFalse(options.allowJoinTeam());
        assertTrue(options.allowJoinTeamHasBeenUpdated());
        assertFalse(ref.get().joinAllowed());
        assertSame(options, ref.get().options());

        options.toggleAllowJoinTeam();

        assertTrue(options.allowJoinTeam());
        assertFalse(options.allowJoinTeamHasBeenUpdated());
        assertTrue(ref.get().joinAllowed());
        assertSame(options, ref.get().options());
    }

    @Test
    void toggleNeedHelp() {
        AtomicReference<NeedHelpChanged> ref = new AtomicReference<>();
        fight.dispatcher().add(NeedHelpChanged.class, ref::set);

        options.toggleNeedHelp();

        assertTrue(options.needHelp());
        assertTrue(options.needHelpHasBeenUpdated());
        assertTrue(ref.get().helpNeeded());
        assertSame(options, ref.get().options());

        options.toggleNeedHelp();

        assertFalse(options.needHelp());
        assertFalse(options.needHelpHasBeenUpdated());
        assertFalse(ref.get().helpNeeded());
        assertSame(options, ref.get().options());
    }
}
