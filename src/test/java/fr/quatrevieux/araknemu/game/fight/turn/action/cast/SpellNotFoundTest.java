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
 * Copyright (c) 2017-2022 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.turn.action.cast;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.PlayableFighter;
import fr.quatrevieux.araknemu.game.fight.module.CommonEffectsModule;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;
import fr.quatrevieux.araknemu.game.fight.turn.action.ActionType;
import fr.quatrevieux.araknemu.network.game.out.info.Error;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class SpellNotFoundTest extends FightBaseCase {
    private Fight fight;
    private FightTurn turn;
    private PlayableFighter fighter;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        fight = createFight();
        fight.nextState();
        fight.register(new CommonEffectsModule(fight));
        turn = new FightTurn(fighter = player.fighter(), fight, Duration.ofSeconds(30));

        fighter.move(fight.map().get(185));
        other.fighter().move(fight.map().get(186));

        turn.start();
    }

    @Test
    void values() {
        SpellNotFound cast = new SpellNotFound(fighter);

        assertSame(fighter, cast.performer());
        assertSame(ActionType.CAST, cast.type());
    }

    @Test
    void validateSpellNotFound() {
        SpellNotFound cast = new SpellNotFound(fighter);

        assertFalse(cast.validate(turn));
        requestStack.assertLast(Error.cantCastNotFound());
    }

    @Test
    void unsupportedMethods() {
        SpellNotFound cast = new SpellNotFound(fighter);

        assertThrows(UnsupportedOperationException.class, cast::start);
        assertThrows(UnsupportedOperationException.class, cast::duration);
    }
}
