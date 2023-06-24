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

package fr.quatrevieux.araknemu.game.fight.castable.effect.handler.invocations;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.ai.FighterAI;
import fr.quatrevieux.araknemu.game.fight.ai.factory.AiFactory;
import fr.quatrevieux.araknemu.game.fight.castable.FightCastScope;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterFactory;
import fr.quatrevieux.araknemu.game.fight.fighter.PlayableFighter;
import fr.quatrevieux.araknemu.game.fight.fighter.invocation.DoubleFighter;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.fight.module.AiModule;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.spell.SpellConstraints;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import fr.quatrevieux.araknemu.game.spell.effect.area.CellArea;
import fr.quatrevieux.araknemu.game.spell.effect.target.SpellEffectTarget;
import fr.quatrevieux.araknemu.network.game.out.fight.action.ActionEffect;
import fr.quatrevieux.araknemu.network.game.out.fight.turn.FighterTurnOrder;
import fr.quatrevieux.araknemu.network.game.out.info.Error;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Duration;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CreateDoubleHandlerTest extends FightBaseCase {
    private Fight fight;
    private PlayerFighter caster;
    private CreateDoubleHandler handler;
    private FightTurn turn;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        fight = createFight();
        fight.register(new AiModule(container.get(AiFactory.class)));
        fight.nextState();

        caster = player.fighter();
        turn = new FightTurn(caster, fight, Duration.ofSeconds(30));
        turn.start();

        handler = new CreateDoubleHandler(container.get(FighterFactory.class), fight);

        requestStack.clear();
    }

    @Test
    void handle() {
        caster.life().alter(caster, -50);
        requestStack.clear();

        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(true);

        FightCastScope scope = makeCastScope(caster, spell, effect, fight.map().get(123));
        handler.handle(scope, scope.effects().get(0));

        Fighter invoc = fight.map().get(123).fighter();

        assertInstanceOf(DoubleFighter.class, invoc);
        assertContains(invoc, fight.fighters().all());
        assertContains(invoc, fight.turnList().fighters());
        assertSame(caster.team(), invoc.team());
        assertEquals(caster.level(), invoc.level());
        assertInstanceOf(FighterAI.class, invoc.attachment(FighterAI.class));

        assertEquals(caster.life().current(), invoc.life().current());
        assertEquals(caster.life().max(), invoc.life().max());

        for (Characteristic characteristic : Characteristic.values()) {
            assertEquals(caster.characteristics().get(characteristic), invoc.characteristics().get(characteristic));
        }

        assertEquals(invoc.id(), invoc.sprite().id());
        assertEquals(invoc.cell().id(), invoc.sprite().cell());
        assertEquals(invoc.orientation(), invoc.sprite().orientation());
        assertEquals(caster.sprite().type(), invoc.sprite().type());
        assertEquals(caster.sprite().gfxId(), invoc.sprite().gfxId());
        assertEquals(caster.sprite().name(), invoc.sprite().name());

        assertEquals("123;1;0;-1;Bob;1;10^100x100;0;50;0,0,0,0;7b;1c8;315;,,,,;245;6;3;0;0;0;0;0;0;0;0;;", invoc.sprite().toString());

        requestStack.assertAll(
            new FighterTurnOrder(fight.turnList()),
            new ActionEffect(180, caster, "+" + invoc.sprite()),
            new ActionEffect(999, caster, (new FighterTurnOrder(fight.turnList())).toString())
        );
    }

    @Test
    void buff() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(true);

        FightCastScope scope = makeCastScope(caster, spell, effect, fight.map().get(123));
        assertThrows(UnsupportedOperationException.class, () -> handler.buff(scope, scope.effects().get(0)));
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

        assertFalse(handler.check(turn, spell, fight.map().get(123)));
        assertEquals(
            new Error(203, 1).toString(),
            handler.validate(turn, spell, fight.map().get(123)).toString()
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

        assertFalse(handler.check(turn, spell, fight.map().get(123)));
        assertEquals(
            new Error(203, 4).toString(),
            handler.validate(turn, spell, fight.map().get(123)).toString()
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

        assertTrue(handler.check(turn, spell, fight.map().get(123)));
    }

    @Test
    void success() {
        Spell spell = Mockito.mock(Spell.class);

        assertTrue(handler.check(turn, spell, fight.map().get(123)));
        assertNull(handler.validate(turn, spell, fight.map().get(123)));
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

        assertTrue(handler.check(turn, spell, fight.map().get(123)));
        assertNull(handler.validate(turn, spell, fight.map().get(123)));
    }
}
