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

package fr.quatrevieux.araknemu.game.listener.fight;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.fighter.event.FighterDie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CheckFightTerminatedTest extends FightBaseCase {
    private Fight fight;
    private CheckFightTerminated listener;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        fight = createFight();
        fight.nextState();
        listener = new CheckFightTerminated(fight);

        fight.dispatcher().remove(CheckFightTerminated.class);
        requestStack.clear();
    }

    @Test
    void onFighterDieAlreadyFighting() {
        listener.on(new FighterDie(player.fighter(), player.fighter()));

        assertTrue(fight.active());
    }

    @Test
    void onFighterDieWillTerminateFight() {
        player.fighter().life().damage(player.fighter(), 1000);

        listener.on(new FighterDie(player.fighter(), player.fighter()));

        assertFalse(fight.active());
    }
}
