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

package fr.quatrevieux.araknemu.game.fight.castable.validator;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.spell.SpellConstraints;
import fr.quatrevieux.araknemu.network.game.out.info.Error;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class TargetCellValidatorTest extends FightBaseCase {
    private Fight fight;
    private Fighter fighter;
    private FightTurn turn;
    private TargetCellValidator validator;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        fight = createFight();
        fighter = player.fighter();
        turn = new FightTurn(fighter, fight, Duration.ofSeconds(30));
        turn.start();

        fighter.move(fight.map().get(185));

        validator = new TargetCellValidator();
    }

    @Test
    void notAvailableCell() {
        Spell spell = Mockito.mock(Spell.class);

        assertFalse(validator.check(turn, spell, fight.map().get(0)));
        assertEquals(
            Error.cantCastCellNotAvailable().toString(),
            validator.validate(turn, spell, fight.map().get(0)).toString()
        );
    }

    @Test
    void cellNotFree() {
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(true);

        assertFalse(validator.check(turn, spell, fight.map().get(185)));
        assertEquals(
            Error.cantCastInvalidCell().toString(),
            validator.validate(turn, spell, fight.map().get(185)).toString()
        );
    }

    @Test
    void freeCell() {
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(true);

        assertTrue(validator.check(turn, spell, fight.map().get(123)));
        assertNull(validator.validate(turn, spell, fight.map().get(123)));
    }

    @Test
    void noNeedFreeCell() {
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        assertTrue(validator.check(turn, spell, fight.map().get(185)));
        assertNull(validator.validate(turn, spell, fight.map().get(185)));
    }
}