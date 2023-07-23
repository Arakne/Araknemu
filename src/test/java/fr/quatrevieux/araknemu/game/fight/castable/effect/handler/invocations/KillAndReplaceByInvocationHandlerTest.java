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
import fr.quatrevieux.araknemu.game.fight.ai.FighterAI;
import fr.quatrevieux.araknemu.game.fight.ai.factory.AiFactory;
import fr.quatrevieux.araknemu.game.fight.castable.FightCastScope;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterFactory;
import fr.quatrevieux.araknemu.game.fight.fighter.invocation.DoubleFighter;
import fr.quatrevieux.araknemu.game.fight.fighter.invocation.InvocationFighter;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.fight.module.AiModule;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;
import fr.quatrevieux.araknemu.game.monster.MonsterService;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.spell.SpellConstraints;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import fr.quatrevieux.araknemu.game.spell.effect.area.CellArea;
import fr.quatrevieux.araknemu.game.spell.effect.target.SpellEffectTarget;
import fr.quatrevieux.araknemu.network.game.out.fight.action.ActionEffect;
import fr.quatrevieux.araknemu.network.game.out.fight.turn.FighterTurnOrder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class KillAndReplaceByInvocationHandlerTest extends FightBaseCase {
    private Fight fight;
    private PlayerFighter caster;
    private KillAndReplaceByInvocationHandler handler;
    private FightTurn turn;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMonsterTemplateInvocations().pushMonsterSpellsInvocations().pushRewardItems();

        fight = fightBuilder()
            .addSelf(fb -> fb.cell(182).charac(Characteristic.INTELLIGENCE, 100))
            .addEnemy(fb -> fb.cell(196))
            .addEnemy(fb -> fb.cell(167))
            .addEnemy(fb -> fb.cell(168))
            .addEnemy(fb -> fb.cell(197))
            .build(true)
        ;

        fight.register(new AiModule(container.get(AiFactory.class)));
        fight.nextState();

        caster = player.fighter();
        turn = new FightTurn(caster, fight, Duration.ofSeconds(30));
        turn.start();

        handler = new KillAndReplaceByInvocationHandler(container.get(MonsterService.class), container.get(FighterFactory.class), fight);

        requestStack.clear();
    }

    @Test
    void handle() {
        Fighter target = fight.map().get(196).fighter();

        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.min()).thenReturn(36); // bouftou
        Mockito.when(effect.max()).thenReturn(1); // grade 1
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);

        FightCastScope scope = makeCastScope(caster, spell, effect, fight.map().get(196));
        handler.handle(scope, scope.effects().get(0));

        Fighter invoc = fight.map().get(196).fighter();

        assertInstanceOf(InvocationFighter.class, invoc);
        assertContains(invoc, fight.fighters().all());
        assertContains(invoc, fight.turnList().fighters());
        assertSame(caster.team(), invoc.team());
        assertEquals(1, invoc.level());
        assertEquals(36, ((InvocationFighter) invoc).monster().id());
        assertInstanceOf(FighterAI.class, invoc.attachment(FighterAI.class));
        assertTrue(target.dead());

        requestStack.assertAll(
            ActionEffect.fighterDie(caster, target),
            new FighterTurnOrder(fight.turnList()),
            new ActionEffect(181, caster, "+" + invoc.sprite()),
            new ActionEffect(999, caster, (new FighterTurnOrder(fight.turnList())).toString())
        );
    }

    @Test
    void handleMaxInvocationCountReachShouldOnlyKill() {
        DoubleFighter invoc = new DoubleFighter(-5, caster);
        fight.fighters().joinTurnList(invoc, fight.map().get(123));
        invoc.init();
        requestStack.clear();

        Fighter target = fight.map().get(196).fighter();

        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.min()).thenReturn(36); // bouftou
        Mockito.when(effect.max()).thenReturn(1); // grade 1
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);

        FightCastScope scope = makeCastScope(caster, spell, effect, fight.map().get(196));
        handler.handle(scope, scope.effects().get(0));

        assertFalse(fight.map().get(196).hasFighter());
        assertTrue(target.dead());

        requestStack.assertAll(
            ActionEffect.fighterDie(caster, target)
        );
    }

    @Test
    void handleWithoutTargetShouldDoNothing() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.min()).thenReturn(36); // bouftou
        Mockito.when(effect.max()).thenReturn(1); // grade 1
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);

        FightCastScope scope = makeCastScope(caster, spell, effect, fight.map().get(123));
        handler.handle(scope, scope.effects().get(0));

        assertFalse(fight.map().get(123).hasFighter());

        requestStack.assertEmpty();
    }

    @Test
    void buff() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.min()).thenReturn(36); // bouftou
        Mockito.when(effect.max()).thenReturn(1); // grade 1
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(true);

        FightCastScope scope = makeCastScope(caster, spell, effect, fight.map().get(123));
        assertThrows(UnsupportedOperationException.class, () -> handler.buff(scope, scope.effects().get(0)));
    }
//
//    @Test
//    void maxInvocReached() {
//        Spell spell = Mockito.mock(Spell.class);
//        PlayableFighter invoc = Mockito.mock(PlayableFighter.class);
//        Mockito.when(invoc.invoker()).thenReturn(caster);
//        fight.turnList().add(invoc);
//
//        SpellEffect effect = Mockito.mock(SpellEffect.class);
//        Mockito.when(effect.effect()).thenReturn(180);
//        Mockito.when(spell.effects()).thenReturn(Collections.singletonList(effect));
//
//        assertFalse(handler.check(turn, spell, fight.map().get(123)));
//        assertEquals(
//            new Error(203, 1).toString(),
//            handler.validate(turn, spell, fight.map().get(123)).toString()
//        );
//    }
//
//    @Test
//    void maxInvocReachedWithMultipleInvoc() {
//        Spell spell = Mockito.mock(Spell.class);
//        PlayableFighter invoc = Mockito.mock(PlayableFighter.class);
//        Mockito.when(invoc.invoker()).thenReturn(caster);
//
//        // Add 4 invoc
//        fight.turnList().add(invoc);
//        fight.turnList().add(invoc);
//        fight.turnList().add(invoc);
//        fight.turnList().add(invoc);
//
//        SpellEffect effect = Mockito.mock(SpellEffect.class);
//        Mockito.when(effect.effect()).thenReturn(180);
//        Mockito.when(spell.effects()).thenReturn(Collections.singletonList(effect));
//
//        caster.characteristics().alter(Characteristic.MAX_SUMMONED_CREATURES, 3);
//
//        assertFalse(handler.check(turn, spell, fight.map().get(123)));
//        assertEquals(
//            new Error(203, 4).toString(),
//            handler.validate(turn, spell, fight.map().get(123)).toString()
//        );
//    }
//
//    @Test
//    void shouldIgnoreStaticInvocation() {
//        Spell spell = Mockito.mock(Spell.class);
//
//        Fighter invoc1 = Mockito.mock(Fighter.class);
//        Fighter invoc2 = Mockito.mock(Fighter.class);
//        Fighter invoc3 = Mockito.mock(Fighter.class);
//        Fighter invoc4 = Mockito.mock(Fighter.class);
//
//        Mockito.when(invoc1.invoker()).thenReturn(caster);
//        Mockito.when(invoc2.invoker()).thenReturn(caster);
//        Mockito.when(invoc3.invoker()).thenReturn(caster);
//        Mockito.when(invoc4.invoker()).thenReturn(caster);
//
//        fight.fighters().join(invoc1, fight.map().get(146));
//        fight.fighters().join(invoc2, fight.map().get(146));
//        fight.fighters().join(invoc3, fight.map().get(146));
//        fight.fighters().join(invoc4, fight.map().get(146));
//
//        SpellEffect effect = Mockito.mock(SpellEffect.class);
//        Mockito.when(effect.effect()).thenReturn(180);
//        Mockito.when(spell.effects()).thenReturn(Collections.singletonList(effect));
//
//        caster.characteristics().alter(Characteristic.MAX_SUMMONED_CREATURES, 3);
//
//        assertTrue(handler.check(turn, spell, fight.map().get(123)));
//    }
//
//    @Test
//    void success() {
//        Spell spell = Mockito.mock(Spell.class);
//
//        assertTrue(handler.check(turn, spell, fight.map().get(123)));
//        assertNull(handler.validate(turn, spell, fight.map().get(123)));
//    }
//
//    @Test
//    void successWithMultipleInvoc() {Spell spell = Mockito.mock(Spell.class);
//        PlayableFighter invoc = Mockito.mock(PlayableFighter.class);
//        Mockito.when(invoc.invoker()).thenReturn(caster);
//        fight.turnList().add(invoc);
//
//        SpellEffect effect = Mockito.mock(SpellEffect.class);
//        Mockito.when(effect.effect()).thenReturn(180);
//        Mockito.when(spell.effects()).thenReturn(Collections.singletonList(effect));
//
//        caster.characteristics().alter(Characteristic.MAX_SUMMONED_CREATURES, 3);
//
//        assertTrue(handler.check(turn, spell, fight.map().get(123)));
//        assertNull(handler.validate(turn, spell, fight.map().get(123)));
//    }
}
