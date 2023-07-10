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
 * Copyright (c) 2017-2023 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.module;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.fighter.invocation.DoubleFighter;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SpiritualLeashModuleTest extends FightBaseCase {
    private Fight fight;
    private SpiritualLeashModule module;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        fight = createFight(true);
        fight.nextState();
        fight.register(module = new SpiritualLeashModule(fight));
    }

    @Test
    void withoutDeadFighters() {
        assertNull(module.getLastDeadFighter(player.fighter().team()));
        assertFalse(module.hasDeadFighter(player.fighter().team()));
    }

    @Test
    void withDeadPlayerFighter() throws SQLException {
        PlayerFighter fighter = makePlayerFighter(makeSimpleGamePlayer(42));
        fighter.setTeam(player.fighter().team());
        fight.fighters().joinTurnList(fighter, fight.map().get(123));
        fighter.init();

        fighter.life().kill(fighter);

        assertTrue(module.hasDeadFighter(player.fighter().team()));
        assertFalse(module.hasDeadFighter(other.fighter().team()));

        assertNull(module.getLastDeadFighter(other.fighter().team()));
        assertSame(fighter, module.getLastDeadFighter(player.fighter().team()));

        assertFalse(module.hasDeadFighter(player.fighter().team()));
    }

    @Test
    void withMultipleDeadFighterShouldReturnTheLastOne() throws SQLException {
        PlayerFighter fighter1 = makePlayerFighter(makeSimpleGamePlayer(42));
        PlayerFighter fighter2 = makePlayerFighter(makeSimpleGamePlayer(43));
        fighter1.setTeam(player.fighter().team());
        fighter2.setTeam(player.fighter().team());
        fight.fighters().joinTurnList(fighter1, fight.map().get(123));
        fight.fighters().joinTurnList(fighter2, fight.map().get(124));
        fighter1.init();
        fighter2.init();

        fighter1.life().kill(fighter1);
        fighter2.life().kill(fighter2);

        assertTrue(module.hasDeadFighter(player.fighter().team()));
        assertSame(fighter2, module.getLastDeadFighter(player.fighter().team()));
        assertTrue(module.hasDeadFighter(player.fighter().team()));
        assertSame(fighter1, module.getLastDeadFighter(player.fighter().team()));
        assertFalse(module.hasDeadFighter(player.fighter().team()));
    }

    @Test
    void shouldPrioritizeActualFighterToInvocations() throws SQLException {
        PlayerFighter fighter1 = makePlayerFighter(makeSimpleGamePlayer(42));
        fighter1.setTeam(player.fighter().team());
        DoubleFighter fighter2 = new DoubleFighter(-42, player.fighter());
        fight.fighters().joinTurnList(fighter1, fight.map().get(123));
        fight.fighters().joinTurnList(fighter2, fight.map().get(124));
        fighter1.init();
        fighter2.init();

        fighter1.life().kill(fighter1);
        fighter2.life().kill(fighter2);

        assertTrue(module.hasDeadFighter(player.fighter().team()));
        assertSame(fighter1, module.getLastDeadFighter(player.fighter().team()));
        assertTrue(module.hasDeadFighter(player.fighter().team()));
        assertSame(fighter2, module.getLastDeadFighter(player.fighter().team()));
        assertFalse(module.hasDeadFighter(player.fighter().team()));
    }

    @Test
    void shouldIgnoreAlreadyResuscitatedFighters() throws SQLException {
        PlayerFighter fighter1 = makePlayerFighter(makeSimpleGamePlayer(42));
        fighter1.setTeam(player.fighter().team());
        DoubleFighter fighter2 = new DoubleFighter(-42, player.fighter());
        fight.fighters().joinTurnList(fighter1, fight.map().get(123));
        fight.fighters().joinTurnList(fighter2, fight.map().get(124));
        fighter1.init();
        fighter2.init();

        fighter1.life().kill(fighter1);
        fighter2.life().kill(fighter2);

        fighter1.life().resuscitate(fighter1, 100);
        fighter2.life().resuscitate(fighter2, 100);

        assertFalse(module.hasDeadFighter(player.fighter().team()));
        assertNull(module.getLastDeadFighter(player.fighter().team()));
    }

    @Test
    void shouldRemoveLeavingFighters() throws SQLException {
        PlayerFighter fighter = makePlayerFighter(makeSimpleGamePlayer(42));
        fighter.setTeam(player.fighter().team());
        fight.fighters().joinTurnList(fighter, fight.map().get(123));
        fighter.init();

        fighter.life().kill(fighter);
        assertTrue(module.hasDeadFighter(player.fighter().team()));

        fight.fighters().leave(fighter);

        assertFalse(module.hasDeadFighter(player.fighter().team()));
        assertNull(module.getLastDeadFighter(player.fighter().team()));
    }
}
