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

package fr.quatrevieux.araknemu.game.fight.module;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.castable.spell.LaunchedSpells;
import fr.quatrevieux.araknemu.game.fight.castable.spell.SpellConstraintsValidator;
import fr.quatrevieux.araknemu.game.fight.fighter.ActiveFighter;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;
import fr.quatrevieux.araknemu.game.fight.turn.action.cast.Cast;
import fr.quatrevieux.araknemu.game.fight.turn.action.util.CriticalityStrategy;
import fr.quatrevieux.araknemu.game.fight.turn.order.AlternateTeamFighterOrder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LaunchedSpellsModuleTest extends FightBaseCase {
    @Test
    void fighterInitialized() throws Exception {
        Fight fight = createFight(false);
        fight.register(new LaunchedSpellsModule(fight));
        fight.nextState();

        player.fighter().init();

        assertInstanceOf(LaunchedSpells.class, player.fighter().attachment(LaunchedSpells.class));
    }

    @Test
    void spellCasted() throws Exception {
        Fight fight = createFight(false);
        fight.register(new LaunchedSpellsModule(fight));
        fight.nextState();
        fight.start(new AlternateTeamFighterOrder());
        fight.turnList().start();

        FightTurn currentTurn = fight.turnList().current().get();

        currentTurn.perform(new Cast(
            currentTurn.fighter(),
            currentTurn.fighter().spells().get(6),
            player.fighter().cell(),
            new SpellConstraintsValidator(),

            // Ensure no critical hit / fail
            new CriticalityStrategy() {
                public int hitRate(ActiveFighter fighter, int base) { return 0; }
                public int failureRate(ActiveFighter fighter, int base) { return 0; }
                public boolean hit(ActiveFighter fighter, int baseRate) { return false; }
                public boolean failed(ActiveFighter fighter, int baseRate) { return false; }
            }
        ));
        currentTurn.terminate();

        assertFalse(player.fighter().attachment(LaunchedSpells.class).valid(player.fighter().spells().get(6), player.fighter().cell()));
    }

    @Test
    void turnTerminated() throws Exception {
        Fight fight = createFight(false);
        fight.register(new LaunchedSpellsModule(fight));
        fight.nextState();
        fight.start(new AlternateTeamFighterOrder());
        fight.turnList().start();

        player.fighter().attachment(LaunchedSpells.class).push(player.fighter().spells().get(6), player.fighter().cell());
        assertFalse(player.fighter().attachment(LaunchedSpells.class).valid(player.fighter().spells().get(6), player.fighter().cell()));

        for (int i = 0; i < 5; ++i) {
            player.fighter().turn().stop();
            other.fighter().turn().stop();
        }

        assertTrue(player.fighter().attachment(LaunchedSpells.class).valid(player.fighter().spells().get(6), player.fighter().cell()));
    }
}
