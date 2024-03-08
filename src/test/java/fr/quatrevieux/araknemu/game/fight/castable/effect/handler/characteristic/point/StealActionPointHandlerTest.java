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
import fr.quatrevieux.araknemu.game.fight.castable.FightCastScope;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buff;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.BuffHook;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.spell.SpellConstraints;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import fr.quatrevieux.araknemu.game.spell.effect.area.CellArea;
import fr.quatrevieux.araknemu.game.spell.effect.area.CircleArea;
import fr.quatrevieux.araknemu.game.spell.effect.target.SpellEffectTarget;
import fr.quatrevieux.araknemu.network.game.out.account.Stats;
import fr.quatrevieux.araknemu.network.game.out.fight.AddBuff;
import fr.quatrevieux.araknemu.network.game.out.fight.action.ActionEffect;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StealActionPointHandlerTest extends FightBaseCase {
    private Fight fight;
    private PlayerFighter caster;
    private PlayerFighter target;
    private StealActionPointHandler handler;

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

        handler = new StealActionPointHandler(fight, 101, 111);

        requestStack.clear();
    }

    @Test
    void buffFixed() {
        caster.characteristics().alter(Characteristic.WISDOM, 10000);
        requestStack.clear();

        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.effect()).thenReturn(84);
        Mockito.when(effect.min()).thenReturn(2);
        Mockito.when(effect.duration()).thenReturn(5);
        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        FightCastScope scope = makeCastScope(caster, spell, effect, target.cell());
        handler.buff(scope, scope.effects().get(0));

        Optional<Buff> buffT = target.buffs().stream().filter(b -> b.effect().effect() == 101).findFirst();
        Optional<Buff> buffC = caster.buffs().stream().filter(b -> b.effect().effect() == 111).findFirst();

        assertTrue(buffT.isPresent());
        assertTrue(buffC.isPresent());
        assertEquals(2, buffT.get().effect().min());
        assertEquals(2, buffC.get().effect().min());
        assertEquals(4, target.characteristics().get(Characteristic.ACTION_POINT));
        assertEquals(8, caster.characteristics().get(Characteristic.ACTION_POINT));
        assertEquals(8, caster.turn().points().actionPoints());

        requestStack.assertAll(
            ActionEffect.buff(buffT.get(), -2),
            new AddBuff(buffT.get()),
            new Stats(caster.properties()),
            ActionEffect.buff(buffC.get(), 2),
            "GIE111;1;2;;0;;5;0"
        );
    }

    @Test
    void directEffectShouldBeTransformedToSingleTurnBuffForTargetAndAddTurnPointToCaster() {
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

        FightCastScope scope = makeCastScope(caster, spell, effect, target.cell());
        handler.handle(scope, scope.effects().get(0));


        Optional<Buff> buffT = target.buffs().stream().filter(b -> b.effect().effect() == 101).findFirst();
        Optional<Buff> buffC = caster.buffs().stream().filter(b -> b.effect().effect() == 111).findFirst();

        assertTrue(buffT.isPresent());
        assertFalse(buffC.isPresent());
        assertEquals(2, buffT.get().effect().min());
        assertEquals(1, buffT.get().remainingTurns());
        assertEquals(4, target.characteristics().get(Characteristic.ACTION_POINT));
        assertEquals(6, caster.characteristics().get(Characteristic.ACTION_POINT));
        assertEquals(8, caster.turn().points().actionPoints());

        requestStack.assertAll(
            ActionEffect.buff(buffT.get(), -2),
            new AddBuff(buffT.get()),
            ActionEffect.addActionPoints(caster, 2)
        );
    }

    @Test
    void buffShouldCallOnCharacteristicAltered() {
        caster.characteristics().alter(Characteristic.WISDOM, 10000);
        AtomicReference<Characteristic> hookCharacteristic = new AtomicReference<>();
        AtomicReference<Integer> hookValue = new AtomicReference<>();

        target.buffs().add(new Buff(Mockito.mock(SpellEffect.class), Mockito.mock(Spell.class), target, target, new BuffHook() {
            @Override
            public void onCharacteristicAltered(Buff buff, Characteristic characteristic, int value) {
                hookCharacteristic.set(characteristic);
                hookValue.set(value);
            }
        }, true));

        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.effect()).thenReturn(111);
        Mockito.when(effect.min()).thenReturn(2);
        Mockito.when(effect.duration()).thenReturn(5);
        Mockito.when(effect.area()).thenReturn(new CircleArea(new EffectArea(EffectArea.Type.CIRCLE, 10)));
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        FightCastScope scope = makeCastScope(caster, spell, effect, target.cell());
        handler.buff(scope, scope.effects().get(0));

        assertEquals(Characteristic.ACTION_POINT, hookCharacteristic.get());
        assertEquals(-2, hookValue.get());
    }

    @Test
    void applyOnSelfShouldBeIgnored() {
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

        FightCastScope scope = makeCastScope(caster, spell, effect, caster.cell());
        handler.buff(scope, scope.effects().get(0));

        Optional<Buff> buff = caster.buffs().stream().filter(b -> b.effect().effect() == 101).findFirst();

        assertFalse(buff.isPresent());
        requestStack.assertEmpty();
    }

    @Test
    void dodgedAllShouldNotAddBuff() {
        target.characteristics().alter(Characteristic.RESISTANCE_ACTION_POINT, 1000);
        requestStack.clear();

        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.effect()).thenReturn(84);
        Mockito.when(effect.min()).thenReturn(2);
        Mockito.when(effect.duration()).thenReturn(5);
        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        FightCastScope scope = makeCastScope(caster, spell, effect, target.cell());
        handler.buff(scope, scope.effects().get(0));

        assertFalse(target.buffs().stream().anyMatch(b -> b.effect().effect() == 101));
        assertFalse(caster.buffs().stream().anyMatch(b -> b.effect().effect() == 111));

        assertEquals(6, target.characteristics().get(Characteristic.ACTION_POINT));
        assertEquals(6, caster.characteristics().get(Characteristic.ACTION_POINT));
        assertEquals(6, caster.turn().points().actionPoints());

        requestStack.assertAll(new ActionEffect(308, caster, target.id(), 2));
        requestStack.assertNotContains(AddBuff.class);
    }

    @Test
    void partialDodge() {
        requestStack.clear();

        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.effect()).thenReturn(84);
        Mockito.when(effect.min()).thenReturn(3);
        Mockito.when(effect.duration()).thenReturn(5);
        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        FightCastScope scope = makeCastScope(caster, spell, effect, target.cell());
        handler.buff(scope, scope.effects().get(0));

        Optional<Buff> buffT = target.buffs().stream().filter(b -> b.effect().effect() == 101).findFirst();
        Optional<Buff> buffC = caster.buffs().stream().filter(b -> b.effect().effect() == 111).findFirst();

        assertTrue(buffT.isPresent());
        assertTrue(buffC.isPresent());
        assertBetween(1, 2, buffT.get().effect().min());
        assertBetween(1, 2, buffC.get().effect().min());
        assertEquals(6 - buffT.get().effect().min(), target.characteristics().get(Characteristic.ACTION_POINT));
        assertEquals(6 + buffC.get().effect().min(), caster.characteristics().get(Characteristic.ACTION_POINT));
        assertEquals(6 + buffC.get().effect().min(), caster.turn().points().actionPoints());

        requestStack.assertAll(
            new ActionEffect(308, caster, target.id(), 3 - buffT.get().effect().min()),
            ActionEffect.buff(buffT.get(), -buffT.get().effect().min()),
            new AddBuff(buffT.get()),
            new Stats(caster.properties()),
            ActionEffect.buff(buffC.get(), buffC.get().effect().min()),
            "GIE111;1;2;;0;;5;0"
        );
    }

    @Test
    void withArea() {
        fight.cancel(true);

        configureFight(fb -> fb
            .addSelf(b -> b.cell(165))
            .addEnemy(b -> b.cell(150))
            .addEnemy(b -> b.cell(151))
        );

        requestStack.clear();

        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.effect()).thenReturn(84);
        Mockito.when(effect.min()).thenReturn(1000);
        Mockito.when(effect.duration()).thenReturn(5);
        Mockito.when(effect.area()).thenReturn(new CircleArea(new EffectArea(EffectArea.Type.CIRCLE, 10)));
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        FightCastScope scope = makeCastScope(getFighter(0), spell, effect, fight.map().get(150));
        handler.buff(scope, scope.effects().get(0));

        Optional<Buff> buff0 = getFighter(0).buffs().stream().filter(b -> b.effect().effect() == 111).findFirst();
        Optional<Buff> buff1 = getFighter(1).buffs().stream().filter(b -> b.effect().effect() == 101).findFirst();
        Optional<Buff> buff2 = getFighter(2).buffs().stream().filter(b -> b.effect().effect() == 101).findFirst();

        assertTrue(buff0.isPresent());
        assertTrue(buff1.isPresent());
        assertTrue(buff2.isPresent());

        assertEquals(18, getFighter(0).characteristics().get(Characteristic.ACTION_POINT));
        assertEquals(0, getFighter(1).characteristics().get(Characteristic.ACTION_POINT));
        assertEquals(0, getFighter(2).characteristics().get(Characteristic.ACTION_POINT));
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

        FightCastScope scope = makeCastScope(caster, spell, effect, target.cell());
        handler.buff(scope, scope.effects().get(0));

        assertEquals(4, target.characteristics().get(Characteristic.ACTION_POINT));
        assertEquals(8, caster.characteristics().get(Characteristic.ACTION_POINT));

        requestStack.clear();

        target.buffs().refresh();
        caster.buffs().refresh();
        caster.buffs().refresh();

        assertEquals(6, target.characteristics().get(Characteristic.ACTION_POINT));
        assertEquals(6, caster.characteristics().get(Characteristic.ACTION_POINT));
    }

    private void configureFight(Consumer<FightBuilder> configurator) {
        FightBuilder builder = fightBuilder();
        configurator.accept(builder);

        fight = builder.build(true);
        handler = new StealActionPointHandler(fight, 101, 111);

        fight.nextState();

        requestStack.clear();
    }
}
