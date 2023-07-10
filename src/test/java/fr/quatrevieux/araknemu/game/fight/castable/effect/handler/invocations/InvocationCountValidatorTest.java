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

package fr.quatrevieux.araknemu.game.fight.castable.effect.handler.invocations;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.ai.factory.AiFactory;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.PlayableFighter;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.fight.module.AiModule;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import fr.quatrevieux.araknemu.network.game.out.info.Error;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Duration;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InvocationCountValidatorTest extends FightBaseCase {
    private Fight fight;
    private PlayerFighter caster;
    private InvocationCountValidator validator;
    private FightTurn turn;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMonsterTemplateInvocations().pushMonsterSpellsInvocations().pushRewardItems();

        fight = createFight();
        fight.register(new AiModule(container.get(AiFactory.class)));
        fight.nextState();

        caster = player.fighter();
        turn = new FightTurn(caster, fight, Duration.ofSeconds(30));
        turn.start();

        validator = new InvocationCountValidator(fight);

        requestStack.clear();
    }


    @Test
    void maxInvocReached() {
        Spell spell = Mockito.mock(Spell.class);
        PlayableFighter invoc = Mockito.mock(PlayableFighter.class);
        Mockito.when(invoc.invoker()).thenReturn(caster);
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
        PlayableFighter invoc = Mockito.mock(PlayableFighter.class);
        Mockito.when(invoc.invoker()).thenReturn(caster);

        // Add 4 invoc
        fight.turnList().add(invoc);
        fight.turnList().add(invoc);
        fight.turnList().add(invoc);
        fight.turnList().add(invoc);

        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Mockito.when(effect.effect()).thenReturn(180);
        Mockito.when(spell.effects()).thenReturn(Collections.singletonList(effect));

        caster.characteristics().alter(Characteristic.MAX_SUMMONED_CREATURES, 3);

        assertFalse(validator.check(turn, spell, fight.map().get(123)));
        assertEquals(
            new Error(203, 4).toString(),
            validator.validate(turn, spell, fight.map().get(123)).toString()
        );
    }

    @Test
    void shouldIgnoreStaticInvocation() {
        Spell spell = Mockito.mock(Spell.class);

        Fighter invoc1 = Mockito.mock(Fighter.class);
        Fighter invoc2 = Mockito.mock(Fighter.class);
        Fighter invoc3 = Mockito.mock(Fighter.class);
        Fighter invoc4 = Mockito.mock(Fighter.class);

        Mockito.when(invoc1.invoker()).thenReturn(caster);
        Mockito.when(invoc2.invoker()).thenReturn(caster);
        Mockito.when(invoc3.invoker()).thenReturn(caster);
        Mockito.when(invoc4.invoker()).thenReturn(caster);

        fight.fighters().join(invoc1, fight.map().get(146));
        fight.fighters().join(invoc2, fight.map().get(146));
        fight.fighters().join(invoc3, fight.map().get(146));
        fight.fighters().join(invoc4, fight.map().get(146));

        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Mockito.when(effect.effect()).thenReturn(180);
        Mockito.when(spell.effects()).thenReturn(Collections.singletonList(effect));

        caster.characteristics().alter(Characteristic.MAX_SUMMONED_CREATURES, 3);

        assertTrue(validator.check(turn, spell, fight.map().get(123)));
    }

    @Test
    void success() {
        Spell spell = Mockito.mock(Spell.class);

        assertTrue(validator.check(turn, spell, fight.map().get(123)));
        assertNull(validator.validate(turn, spell, fight.map().get(123)));
    }

    @Test
    void successWithMultipleInvoc() {Spell spell = Mockito.mock(Spell.class);
        PlayableFighter invoc = Mockito.mock(PlayableFighter.class);
        Mockito.when(invoc.invoker()).thenReturn(caster);
        fight.turnList().add(invoc);

        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Mockito.when(effect.effect()).thenReturn(180);
        Mockito.when(spell.effects()).thenReturn(Collections.singletonList(effect));

        caster.characteristics().alter(Characteristic.MAX_SUMMONED_CREATURES, 3);

        assertTrue(validator.check(turn, spell, fight.map().get(123)));
        assertNull(validator.validate(turn, spell, fight.map().get(123)));
    }
}
