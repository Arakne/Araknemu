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

package fr.quatrevieux.araknemu.game.listener.fight.fighter;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.exception.FightException;
import fr.quatrevieux.araknemu.game.fight.fighter.event.FighterDie;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.listener.fight.CheckFightTerminated;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RemoveDeadFighterTest extends FightBaseCase {
    private Fight fight;
    private RemoveDeadFighter listener;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        fight = createFight();
        fight.nextState();
        fight.turnList().start();
        listener = new RemoveDeadFighter(fight);

        fight.dispatcher().remove(CheckFightTerminated.class);
        fight.dispatcher().remove(RemoveDeadFighter.class);
        requestStack.clear();
    }

    @Test
    void onFighterDieNotCurrentTurn() {
        FightCell cell = other.fighter().cell();
        listener.on(new FighterDie(other.fighter(), other.fighter()));

        assertFalse(cell.hasFighter());
        assertEquals(cell, other.fighter().cell());
    }

    @Test
    void onFighterDieCurrentTurn() {
        FightCell cell = player.fighter().cell();

        player.fighter().life().damage(player.fighter(), 1000);
        listener.on(new FighterDie(player.fighter(), player.fighter()));

        assertFalse(cell.hasFighter());
        assertThrows(FightException.class, () -> player.fighter().turn());
        assertFalse(fight.turnList().current().get().active());
    }
}