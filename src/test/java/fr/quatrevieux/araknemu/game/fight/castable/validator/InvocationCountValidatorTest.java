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

package fr.quatrevieux.araknemu.game.fight.castable.validator;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.BuffEffect;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;
import fr.quatrevieux.araknemu.game.fight.turn.order.AlternateTeamFighterOrder;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import fr.quatrevieux.araknemu.network.game.out.info.Error;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Duration;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class InvocationCountValidatorTest extends FightBaseCase {
    private Fight fight;
    private Fighter fighter;
    private FightTurn turn;
    private InvocationCountValidator validator;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        fight = createFight();
        fighter = player.fighter();
        turn = new FightTurn(fighter, fight, Duration.ofSeconds(30));
        turn.start();
        fight.turnList().init(new AlternateTeamFighterOrder());

        validator = new InvocationCountValidator();
    }

    @Test
    void maxInvocReached() {
        Spell spell = Mockito.mock(Spell.class);
        Fighter invoc = Mockito.mock(Fighter.class);
        Mockito.when(invoc.invoker()).thenReturn(fighter);
        fight.turnList().add(invoc);

        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Mockito.when(effect.effect()).thenReturn(180);
        Mockito.when(spell.effects()).thenReturn(Collections.singletonList(effect));

        assertFalse(validator.check(turn, spell, fight.map().get(123)));
        assertEquals(
            new Error(203, 1).toString(),
            validator.validate(turn, spell, fight.map().get(123)).toString()
        );
    }

    @Test
    void maxInvocReachedWithMultipleInvoc() {
        Spell spell = Mockito.mock(Spell.class);
        Fighter invoc = Mockito.mock(Fighter.class);
        Mockito.when(invoc.invoker()).thenReturn(fighter);

        // Add 4 invoc
        fight.turnList().add(invoc);
        fight.turnList().add(invoc);
        fight.turnList().add(invoc);
        fight.turnList().add(invoc);

        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Mockito.when(effect.effect()).thenReturn(180);
        Mockito.when(spell.effects()).thenReturn(Collections.singletonList(effect));

        fighter.characteristics().alter(Characteristic.MAX_SUMMONED_CREATURES, 3);

        assertFalse(validator.check(turn, spell, fight.map().get(123)));
        assertEquals(
            new Error(203, 4).toString(),
            validator.validate(turn, spell, fight.map().get(123)).toString()
        );
    }

    @Test
    void success() {
        Spell spell = Mockito.mock(Spell.class);

        assertTrue(validator.check(turn, spell, fight.map().get(123)));
        assertNull(validator.validate(turn, spell, fight.map().get(123)));
    }

    @Test
    void successWithMultipleInvoc() {Spell spell = Mockito.mock(Spell.class);
        Fighter invoc = Mockito.mock(Fighter.class);
        Mockito.when(invoc.invoker()).thenReturn(fighter);
        fight.turnList().add(invoc);

        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Mockito.when(effect.effect()).thenReturn(180);
        Mockito.when(spell.effects()).thenReturn(Collections.singletonList(effect));

        fighter.characteristics().alter(Characteristic.MAX_SUMMONED_CREATURES, 3);

        assertTrue(validator.check(turn, spell, fight.map().get(123)));
        assertNull(validator.validate(turn, spell, fight.map().get(123)));
    }

    @Test
    void ignoreNotInvocationSpell() {
        Spell spell = Mockito.mock(Spell.class);
        Fighter invoc = Mockito.mock(Fighter.class);
        Mockito.when(invoc.invoker()).thenReturn(fighter);
        fight.turnList().add(invoc);

        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Mockito.when(effect.effect()).thenReturn(123);
        Mockito.when(spell.effects()).thenReturn(Collections.singletonList(effect));

        assertTrue(validator.check(turn, spell, fight.map().get(123)));
        assertNull(validator.validate(turn, spell, fight.map().get(123)));
    }
}
