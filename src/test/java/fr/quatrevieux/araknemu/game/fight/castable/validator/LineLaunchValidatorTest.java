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

import fr.arakne.utils.value.Interval;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.fighter.PlayableFighter;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.spell.SpellConstraints;
import fr.quatrevieux.araknemu.network.game.out.info.Error;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LineLaunchValidatorTest extends FightBaseCase {
    private Fight fight;
    private PlayableFighter fighter;
    private FightTurn turn;
    private LineLaunchValidator validator;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        fight = createFight();
        fighter = player.fighter();
        turn = new FightTurn(fighter, fight, Duration.ofSeconds(30));
        turn.start();

        fighter.move(fight.map().get(185));

        validator = new LineLaunchValidator();
    }

    @Test
    void notInLine() {
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.lineLaunch()).thenReturn(true);
        Mockito.when(constraints.range()).thenReturn(new Interval(1, 10));

        assertFalse(validator.check(turn, spell, fight.map().get(186)));
        assertEquals(
            Error.cantCastLineLaunch().toString(),
            validator.validate(turn, spell, fight.map().get(186)).toString()
        );
    }

    @Test
    void noInLineConstraint() {
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.lineLaunch()).thenReturn(false);

        assertTrue(validator.check(turn, spell, fight.map().get(186)));
        assertNull(validator.validate(turn, spell, fight.map().get(186)));
    }

    @Test
    void lowRange() {
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.lineLaunch()).thenReturn(true);
        Mockito.when(constraints.range()).thenReturn(new Interval(1, 1));

        assertTrue(validator.check(turn, spell, fight.map().get(200)));
        assertNull(validator.validate(turn, spell, fight.map().get(200)));
    }

    @Test
    void casterCell() {
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.lineLaunch()).thenReturn(true);
        Mockito.when(constraints.range()).thenReturn(new Interval(0, 10));

        assertTrue(validator.check(turn, spell, fight.map().get(185)));
        assertNull(validator.validate(turn, spell, fight.map().get(185)));
    }

    @Test
    void inLine() {
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.lineLaunch()).thenReturn(true);
        Mockito.when(constraints.range()).thenReturn(new Interval(0, 10));

        assertTrue(validator.check(turn, spell, fight.map().get(227)));
        assertNull(validator.validate(turn, spell, fight.map().get(227)));
    }
}
