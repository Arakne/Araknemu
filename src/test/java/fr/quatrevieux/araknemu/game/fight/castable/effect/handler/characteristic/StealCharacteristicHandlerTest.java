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

package fr.quatrevieux.araknemu.game.fight.castable.effect.handler.characteristic;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.data.value.EffectArea;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.castable.CastScope;
import fr.quatrevieux.araknemu.game.fight.castable.effect.EffectValue;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buff;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.BuffHook;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.PassiveFighter;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StealCharacteristicHandlerTest extends FightBaseCase {
    private Fight fight;
    private PlayerFighter caster;
    private PlayerFighter target;
    private StealCharacteristicHandler handler;

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

        handler = new StealCharacteristicHandler(fight, Characteristic.LUCK, 123, 152);

        requestStack.clear();
    }

    @Test
    void handle() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        CastScope<Fighter> scope = makeCastScope(caster, spell, effect, caster.cell());
        assertThrows(UnsupportedOperationException.class, () -> handler.handle(scope, scope.effects().get(0)));
    }

    @Test
    void buffSingleTarget() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        requestStack.clear();

        Mockito.when(effect.effect()).thenReturn(270);
        Mockito.when(effect.min()).thenReturn(50);
        Mockito.when(effect.min()).thenReturn(60);
        Mockito.when(effect.duration()).thenReturn(5);
        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        CastScope<Fighter> scope = makeCastScope(caster, spell, effect, target.cell());
        handler.buff(scope, scope.effects().get(0));

        Optional<Buff> casterBuff = caster.buffs().stream().filter(buff -> buff.effect().effect() == 123).findFirst();
        Optional<Buff> targetBuff = target.buffs().stream().filter(buff -> buff.effect().effect() == 152).findFirst();

        assertTrue(casterBuff.isPresent());
        assertTrue(targetBuff.isPresent());

        int value = casterBuff.get().effect().min();

        assertBetween(50, 60, value);
        assertEquals(value, targetBuff.get().effect().min());

        requestStack.assertOne(ActionEffect.buff(targetBuff.get(), value));
        requestStack.assertOne(ActionEffect.buff(casterBuff.get(), value));
        requestStack.assertOne(new AddBuff(targetBuff.get()));
        requestStack.assertOne("GIE123;1;" + value + ";;0;;5;0");

        assertEquals(value, caster.characteristics().get(Characteristic.LUCK));
        assertEquals(-value, target.characteristics().get(Characteristic.LUCK));

        caster.buffs().removeAll();
        target.buffs().removeAll();

        assertEquals(0, caster.characteristics().get(Characteristic.LUCK));
        assertEquals(0, target.characteristics().get(Characteristic.LUCK));
    }

    @Test
    void buffSingleTargetMaximized() {
        target.buffs().add(new Buff(Mockito.mock(SpellEffect.class), Mockito.mock(Spell.class), target, target, new BuffHook() {
            @Override
            public void onEffectValueTarget(Buff buff, EffectValue value) {
                value.maximize();
            }
        }));

        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        requestStack.clear();

        Mockito.when(effect.effect()).thenReturn(270);
        Mockito.when(effect.min()).thenReturn(0);
        Mockito.when(effect.min()).thenReturn(100);
        Mockito.when(effect.duration()).thenReturn(5);
        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        CastScope<Fighter> scope = makeCastScope(caster, spell, effect, target.cell());
        handler.buff(scope, scope.effects().get(0));

        Optional<Buff> casterBuff = caster.buffs().stream().filter(buff -> buff.effect().effect() == 123).findFirst();
        Optional<Buff> targetBuff = target.buffs().stream().filter(buff -> buff.effect().effect() == 152).findFirst();

        assertTrue(casterBuff.isPresent());
        assertTrue(targetBuff.isPresent());

        assertEquals(100, casterBuff.get().effect().min());
        assertEquals(100, targetBuff.get().effect().min());

        requestStack.assertOne(ActionEffect.buff(targetBuff.get(), 100));
        requestStack.assertOne(ActionEffect.buff(casterBuff.get(), 100));
        requestStack.assertOne(new AddBuff(targetBuff.get()));
        requestStack.assertOne("GIE123;1;100;;0;;5;0");

        assertEquals(100, caster.characteristics().get(Characteristic.LUCK));
        assertEquals(-100, target.characteristics().get(Characteristic.LUCK));

        caster.buffs().removeAll();
        target.buffs().removeAll();

        assertEquals(0, caster.characteristics().get(Characteristic.LUCK));
        assertEquals(0, target.characteristics().get(Characteristic.LUCK));
    }

    @Test
    void buffNotTargetShouldDoNothing() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        requestStack.clear();

        Mockito.when(effect.effect()).thenReturn(270);
        Mockito.when(effect.min()).thenReturn(50);
        Mockito.when(effect.min()).thenReturn(60);
        Mockito.when(effect.duration()).thenReturn(5);
        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        CastScope<Fighter> scope = makeCastScope(caster, spell, effect, fight.map().get(15));
        handler.buff(scope, scope.effects().get(0));

        Optional<Buff> casterBuff = caster.buffs().stream().filter(buff -> buff.effect().effect() == 123).findFirst();

        assertFalse(casterBuff.isPresent());

        requestStack.assertEmpty();

        assertEquals(0, caster.characteristics().get(Characteristic.LUCK));
    }

    @Test
    void buffMultipleTargets() {
        fight = fightBuilder()
            .addSelf(fb -> fb.cell(384))
            .addEnemy(fb -> fb.cell(255))
            .addEnemy(fb -> fb.cell(241))
            .addEnemy(fb -> fb.cell(270))
            .build(true)
        ;

        fight.nextState();

        handler = new StealCharacteristicHandler(fight, Characteristic.LUCK, 123, 152);

        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        requestStack.clear();

        Mockito.when(effect.effect()).thenReturn(270);
        Mockito.when(effect.min()).thenReturn(10);
        Mockito.when(effect.duration()).thenReturn(5);
        Mockito.when(effect.area()).thenReturn(new CircleArea(new EffectArea(EffectArea.Type.CIRCLE, 3)));
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        CastScope<Fighter> scope = makeCastScope(fight.fighters().get(0), spell, effect, fight.map().get(241));
        handler.buff(scope, scope.effects().get(0));

        requestStack.assertOne(ActionEffect.buff(fight.fighters().get(1).buffs().stream().filter(buff -> buff.effect().effect() == 152).findFirst().get(), 10));
        requestStack.assertOne(new AddBuff(fight.fighters().get(1).buffs().stream().filter(buff -> buff.effect().effect() == 152).findFirst().get()));
        requestStack.assertOne(ActionEffect.buff(fight.fighters().get(2).buffs().stream().filter(buff -> buff.effect().effect() == 152).findFirst().get(), 10));
        requestStack.assertOne(new AddBuff(fight.fighters().get(2).buffs().stream().filter(buff -> buff.effect().effect() == 152).findFirst().get()));
        requestStack.assertOne(ActionEffect.buff(fight.fighters().get(3).buffs().stream().filter(buff -> buff.effect().effect() == 152).findFirst().get(), 10));
        requestStack.assertOne(new AddBuff(fight.fighters().get(3).buffs().stream().filter(buff -> buff.effect().effect() == 152).findFirst().get()));

        requestStack.assertOne(ActionEffect.buff(fight.fighters().get(0).buffs().stream().filter(buff -> buff.effect().effect() == 123).findFirst().get(), 30));
        requestStack.assertOne("GIE123;1;30;;0;;5;0");

        assertEquals(30, fight.fighters().get(0).characteristics().get(Characteristic.LUCK));
        assertEquals(-10, fight.fighters().get(1).characteristics().get(Characteristic.LUCK));
        assertEquals(-10, fight.fighters().get(2).characteristics().get(Characteristic.LUCK));
        assertEquals(-10, fight.fighters().get(3).characteristics().get(Characteristic.LUCK));

        fight.fighters().forEach(fighter -> fighter.buffs().removeAll());

        assertEquals(0, fight.fighters().get(0).characteristics().get(Characteristic.LUCK));
        assertEquals(0, fight.fighters().get(1).characteristics().get(Characteristic.LUCK));
        assertEquals(0, fight.fighters().get(2).characteristics().get(Characteristic.LUCK));
        assertEquals(0, fight.fighters().get(3).characteristics().get(Characteristic.LUCK));
    }
}
