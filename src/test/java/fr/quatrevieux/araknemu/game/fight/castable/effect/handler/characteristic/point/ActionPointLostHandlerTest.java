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
 * Copyright (c) 2017-2021 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.castable.effect.handler.characteristic.point;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.data.value.EffectArea;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.castable.CastScope;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buff;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.spell.SpellConstraints;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import fr.quatrevieux.araknemu.game.spell.effect.area.CellArea;
import fr.quatrevieux.araknemu.game.spell.effect.area.CircleArea;
import fr.quatrevieux.araknemu.game.spell.effect.target.SpellEffectTarget;
import fr.quatrevieux.araknemu.network.game.out.fight.AddBuff;
import fr.quatrevieux.araknemu.network.game.out.fight.action.ActionEffect;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ActionPointLostHandlerTest extends FightBaseCase {
    private Fight fight;
    private PlayerFighter caster;
    private PlayerFighter target;
    private ActionPointLostHandler handler;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        fight = createFight();
        fight.nextState();
        fight.turnList().start();

        caster = player.fighter();
        target = other.fighter();

        target.move(fight.map().get(123));

        handler = new ActionPointLostHandler(fight);

        requestStack.clear();
    }

    @Test
    void buffFixed() {
        caster.characteristics().alter(Characteristic.WISDOM, 10000);
        requestStack.clear();

        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.effect()).thenReturn(101);
        Mockito.when(effect.min()).thenReturn(2);
        Mockito.when(effect.duration()).thenReturn(5);
        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        CastScope scope = makeCastScope(caster, spell, effect, target.cell());
        handler.buff(scope, scope.effects().get(0));

        Optional<Buff> buff = target.buffs().stream().filter(b -> b.effect().effect() == 101).findFirst();

        assertTrue(buff.isPresent());
        assertEquals(2, buff.get().effect().min());
        assertEquals(4, target.characteristics().get(Characteristic.ACTION_POINT));

        requestStack.assertOne(ActionEffect.buff(buff.get(), -2));
        requestStack.assertOne(new AddBuff(buff.get()));
    }

    @Test
    void directEffectShouldBeTransformedToSingleTurnBuff() {
        caster.characteristics().alter(Characteristic.WISDOM, 10000);
        requestStack.clear();

        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.effect()).thenReturn(101);
        Mockito.when(effect.min()).thenReturn(2);
        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        CastScope scope = makeCastScope(caster, spell, effect, target.cell());
        handler.handle(scope, scope.effects().get(0));

        Optional<Buff> buff = target.buffs().stream().filter(b -> b.effect().effect() == 101).findFirst();

        assertTrue(buff.isPresent());
        assertEquals(2, buff.get().effect().min());
        assertEquals(1, buff.get().remainingTurns());
        assertEquals(4, target.characteristics().get(Characteristic.ACTION_POINT));

        requestStack.assertOne(ActionEffect.buff(buff.get(), -2));
        requestStack.assertOne(new AddBuff(buff.get()));
    }

    @Test
    void applyOnSelfShouldRemovePointsFromCurrentTurn() {
        requestStack.clear();

        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.effect()).thenReturn(101);
        Mockito.when(effect.min()).thenReturn(2);
        Mockito.when(effect.duration()).thenReturn(2);
        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        CastScope scope = makeCastScope(caster, spell, effect, caster.cell());
        handler.buff(scope, scope.effects().get(0));

        Optional<Buff> buff = caster.buffs().stream().filter(b -> b.effect().effect() == 101).findFirst();

        assertTrue(buff.isPresent());
        assertEquals(1, buff.get().effect().min());
        assertEquals(3, buff.get().remainingTurns());
        assertEquals(5, caster.characteristics().get(Characteristic.ACTION_POINT));
        assertEquals(5, caster.turn().points().actionPoints());

        requestStack.assertOne(ActionEffect.buff(buff.get(), -1));
        requestStack.assertOne("GIE101;1;1;;0;null;2;0");
    }

    @Test
    void dodgedAllShouldNotAddBuff() {
        target.characteristics().alter(Characteristic.RESISTANCE_ACTION_POINT, 1000);
        requestStack.clear();

        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.effect()).thenReturn(101);
        Mockito.when(effect.min()).thenReturn(2);
        Mockito.when(effect.duration()).thenReturn(5);
        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        CastScope scope = makeCastScope(caster, spell, effect, target.cell());
        handler.buff(scope, scope.effects().get(0));

        Optional<Buff> buff = target.buffs().stream().filter(b -> b.effect().effect() == 101).findFirst();

        assertFalse(buff.isPresent());
        assertEquals(6, target.characteristics().get(Characteristic.ACTION_POINT));

        requestStack.assertAll(new ActionEffect(308, caster, target.id(), 2));
        requestStack.assertNotContains(AddBuff.class);
    }

    @Test
    void partialDodge() {
        requestStack.clear();

        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.effect()).thenReturn(101);
        Mockito.when(effect.min()).thenReturn(3);
        Mockito.when(effect.duration()).thenReturn(5);
        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        CastScope scope = makeCastScope(caster, spell, effect, target.cell());
        handler.buff(scope, scope.effects().get(0));

        Optional<Buff> buff = target.buffs().stream().filter(b -> b.effect().effect() == 101).findFirst();

        assertTrue(buff.isPresent());
        assertBetween(1, 2, buff.get().effect().min());
        assertEquals(6 - buff.get().effect().min(), target.characteristics().get(Characteristic.ACTION_POINT));

        requestStack.assertAll(
            new ActionEffect(308, caster, target.id(), 3 - buff.get().effect().min()),
            ActionEffect.buff(buff.get(), -buff.get().effect().min()),
            new AddBuff(buff.get())
        );
    }

    @Test
    void withArea() {
        caster.characteristics().alter(Characteristic.WISDOM, 10000);
        requestStack.clear();

        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.effect()).thenReturn(101);
        Mockito.when(effect.min()).thenReturn(1000);
        Mockito.when(effect.duration()).thenReturn(5);
        Mockito.when(effect.area()).thenReturn(new CircleArea(new EffectArea(EffectArea.Type.CIRCLE, 10)));
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        CastScope scope = makeCastScope(caster, spell, effect, target.cell());
        handler.buff(scope, scope.effects().get(0));

        Optional<Buff> buff1 = target.buffs().stream().filter(b -> b.effect().effect() == 101).findFirst();
        Optional<Buff> buff2 = caster.buffs().stream().filter(b -> b.effect().effect() == 101).findFirst();

        assertTrue(buff1.isPresent());
        assertTrue(buff2.isPresent());

        assertEquals(0, caster.characteristics().get(Characteristic.ACTION_POINT));
        assertEquals(0, target.characteristics().get(Characteristic.ACTION_POINT));
    }

    @Test
    void buffStartAndTerminated() {
        caster.characteristics().alter(Characteristic.WISDOM, 10000);
        requestStack.clear();

        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.effect()).thenReturn(101);
        Mockito.when(effect.min()).thenReturn(2);
        Mockito.when(effect.duration()).thenReturn(1);
        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        CastScope scope = makeCastScope(caster, spell, effect, target.cell());
        handler.buff(scope, scope.effects().get(0));

        Optional<Buff> buff = target.buffs().stream().filter(b -> b.effect().effect() == 101).findFirst();

        requestStack.assertAll(
            ActionEffect.buff(buff.get(), -2),
            new AddBuff(buff.get())
        );
        assertEquals(4, target.characteristics().get(Characteristic.ACTION_POINT));

        requestStack.clear();

        target.buffs().refresh();
        assertEquals(6, target.characteristics().get(Characteristic.ACTION_POINT));
    }
}
