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

package fr.quatrevieux.araknemu.game.fight.castable.validator;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.EffectHandler;
import fr.quatrevieux.araknemu.game.fight.fighter.PlayableFighter;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Duration;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EffectHandlersValidatorTest extends FightBaseCase {
    private Fight fight;
    private PlayableFighter fighter;
    private FightTurn turn;
    private EffectHandlersValidator validator;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        fight = createFight();
        fighter = player.fighter();
        turn = new FightTurn(fighter, fight, Duration.ofSeconds(30));
        turn.start();

        validator = new EffectHandlersValidator(fight);
    }

    @Test
    void checkSuccess() {
        EffectHandler handler = Mockito.mock(EffectHandler.class);

        fight.effects().register(1000, handler);

        Mockito.when(handler.check(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(true);
        Mockito.when(handler.validate(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(null);

        Spell spell = Mockito.mock(Spell.class);
        SpellEffect effect = Mockito.mock(SpellEffect.class);

        Mockito.when(spell.effects()).thenReturn(Collections.singletonList(effect));
        Mockito.when(effect.effect()).thenReturn(1000);

        assertTrue(validator.check(turn, spell, fighter.cell()));
        assertNull(validator.validate(turn, spell, fighter.cell()));

        Mockito.verify(handler).check(turn, spell, fighter.cell());
        Mockito.verify(handler).validate(turn, spell, fighter.cell());
    }

    @Test
    void checkFailed() {
        EffectHandler handler = Mockito.mock(EffectHandler.class);

        fight.effects().register(1000, handler);

        Mockito.when(handler.check(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(false);
        Mockito.when(handler.validate(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn("My error");

        Spell spell = Mockito.mock(Spell.class);
        SpellEffect effect = Mockito.mock(SpellEffect.class);

        Mockito.when(spell.effects()).thenReturn(Collections.singletonList(effect));
        Mockito.when(effect.effect()).thenReturn(1000);

        assertFalse(validator.check(turn, spell, fighter.cell()));
        assertEquals("My error", validator.validate(turn, spell, fighter.cell()));

        Mockito.verify(handler).check(turn, spell, fighter.cell());
        Mockito.verify(handler).validate(turn, spell, fighter.cell());
    }
}
