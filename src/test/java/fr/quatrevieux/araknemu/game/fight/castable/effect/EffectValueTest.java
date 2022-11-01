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

package fr.quatrevieux.araknemu.game.fight.castable.effect;

import fr.arakne.utils.value.Interval;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buff;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.BuffHook;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterData;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EffectValueTest extends FightBaseCase {
    @Test
    void defaultsWithRandomEffect() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);

        Mockito.when(effect.min()).thenReturn(5);
        Mockito.when(effect.max()).thenReturn(10);

        EffectValue value = new EffectValue(effect);

        assertBetween(5, 10, value.value());
        assertEquals(new Interval(5, 10), value.interval());
    }

    @Test
    void randomize() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);

        Mockito.when(effect.min()).thenReturn(5);
        Mockito.when(effect.max()).thenReturn(10);

        EffectValue value = new EffectValue(effect);

        value.randomize();

        assertBetween(5, 10, value.value());
        assertEquals(new Interval(5, 10), value.interval());
    }

    @Test
    void roll() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);

        Mockito.when(effect.min()).thenReturn(5);
        Mockito.when(effect.max()).thenReturn(10);

        EffectValue value = new EffectValue(effect);
        value.roll();

        assertBetween(5, 10, value.value());
        assertEquals(value.value(), value.value());
        assertEquals(Interval.of(value.value()), value.interval());
    }

    @Test
    void defaultsWithFixedEffect() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);

        Mockito.when(effect.min()).thenReturn(5);
        Mockito.when(effect.max()).thenReturn(0);

        EffectValue value = new EffectValue(effect);

        assertEquals(5, value.value());
        assertEquals(new Interval(5, 5), value.interval());
    }

    @Test
    void minimizeWithRandomEffect() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);

        Mockito.when(effect.min()).thenReturn(5);
        Mockito.when(effect.max()).thenReturn(10);

        EffectValue value = new EffectValue(effect);
        value.minimize();

        assertEquals(5, value.value());
        assertEquals(new Interval(5, 5), value.interval());
    }

    @Test
    void maximizeWithRandomEffect() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);

        Mockito.when(effect.min()).thenReturn(5);
        Mockito.when(effect.max()).thenReturn(10);

        EffectValue value = new EffectValue(effect);
        value.maximize();

        assertEquals(10, value.value());
        assertEquals(new Interval(10, 10), value.interval());
    }

    @Test
    void minimizeWithFixedEffect() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);

        Mockito.when(effect.min()).thenReturn(5);
        Mockito.when(effect.max()).thenReturn(0);

        EffectValue value = new EffectValue(effect);
        value.minimize();

        assertEquals(5, value.value());
        assertEquals(new Interval(5, 5), value.interval());
    }

    @Test
    void maximizeWithFixedEffect() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);

        Mockito.when(effect.min()).thenReturn(5);
        Mockito.when(effect.max()).thenReturn(0);

        EffectValue value = new EffectValue(effect);
        value.maximize();

        assertEquals(5, value.value());
        assertEquals(new Interval(5, 5), value.interval());
    }

    @Test
    void fixed() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);

        Mockito.when(effect.min()).thenReturn(5);
        Mockito.when(effect.max()).thenReturn(0);

        EffectValue value = new EffectValue(effect);
        value.fixed(5);

        assertEquals(10, value.value());
        assertEquals(new Interval(10, 10), value.interval());
    }

    @Test
    void percent() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);

        Mockito.when(effect.min()).thenReturn(5);
        Mockito.when(effect.max()).thenReturn(0);

        EffectValue value = new EffectValue(effect);
        value.percent(20);

        assertEquals(6, value.value());
        assertEquals(new Interval(6, 6), value.interval());
    }

    @Test
    void boostAndPercent() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);

        Mockito.when(effect.min()).thenReturn(5);
        Mockito.when(effect.max()).thenReturn(0);

        EffectValue value = new EffectValue(effect);
        value
            .boost(5)
            .percent(20)
        ;

        assertEquals(12, value.value());
        assertEquals(new Interval(12, 12), value.interval());
    }

    @Test
    void fixedAndPercent() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);

        Mockito.when(effect.min()).thenReturn(5);
        Mockito.when(effect.max()).thenReturn(0);

        EffectValue value = new EffectValue(effect);
        value
            .fixed(5)
            .percent(20)
        ;

        assertEquals(11, value.value());
        assertEquals(new Interval(11, 11), value.interval());
    }

    @Test
    void fixedAndPercentAndMultiply() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);

        Mockito.when(effect.min()).thenReturn(5);
        Mockito.when(effect.max()).thenReturn(0);

        EffectValue value = new EffectValue(effect);
        value
            .fixed(5)
            .percent(20)
            .multiply(2)
        ;

        assertEquals(22, value.value());
        assertEquals(new Interval(22, 22), value.interval());
    }

    @Test
    void allWithEffectBoost() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);

        Mockito.when(effect.min()).thenReturn(5);
        Mockito.when(effect.max()).thenReturn(0);
        Mockito.when(effect.boost()).thenReturn(3);

        EffectValue value = new EffectValue(effect);
        value
            .fixed(5)
            .percent(25)
            .multiply(2)
            .boost(3)
        ;

        assertEquals(36, value.value());
        assertEquals(new Interval(36, 36), value.interval());
    }

    @Test
    void createShouldCallBuffs() throws Exception {
        Fight fight = createFight(true);

        Fighter caster = player.fighter();
        Fighter target = other.fighter();

        BuffHook casterHook = Mockito.mock(BuffHook.class);
        BuffHook targetHook = Mockito.mock(BuffHook.class);

        Buff casterBuff = new Buff(Mockito.mock(SpellEffect.class), Mockito.mock(Spell.class), caster, caster, casterHook);
        Buff targetBuff = new Buff(Mockito.mock(SpellEffect.class), Mockito.mock(Spell.class), target, target, targetHook);

        caster.buffs().add(casterBuff);
        target.buffs().add(targetBuff);

        SpellEffect effect = Mockito.mock(SpellEffect.class);

        Mockito.when(effect.min()).thenReturn(5);

        EffectValue ev = EffectValue.create(effect, caster, target);

        assertEquals(5, ev.value());

        Mockito.verify(casterHook, Mockito.times(1)).onEffectValueCast(casterBuff, ev);
        Mockito.verify(targetHook, Mockito.times(1)).onEffectValueTarget(targetBuff, ev);
    }

    @Test
    void forEachTargetsShouldCallBuffs() throws Exception {
        Fight fight = createFight(true);

        Fighter caster = player.fighter();
        Fighter target = other.fighter();

        BuffHook casterHook = Mockito.mock(BuffHook.class);
        BuffHook targetHook = Mockito.mock(BuffHook.class);

        Buff casterBuff = new Buff(Mockito.mock(SpellEffect.class), Mockito.mock(Spell.class), caster, caster, casterHook);
        Buff targetBuff = new Buff(Mockito.mock(SpellEffect.class), Mockito.mock(Spell.class), target, target, targetHook);

        caster.buffs().add(casterBuff);
        target.buffs().add(targetBuff);

        SpellEffect effect = Mockito.mock(SpellEffect.class);

        Mockito.when(effect.min()).thenReturn(5);

        List<EffectValue> values = new ArrayList<>();
        List<FighterData> fighters = new ArrayList<>();

        EffectValue.forEachTargets(effect, caster, Arrays.asList(caster, target), ((fighter, value) -> {
            values.add(value);
            fighters.add(fighter);
        }));

        assertIterableEquals(Arrays.asList(caster, target), fighters);
        assertCount(2, values);
        assertNotSame(values.get(0), values.get(1));
        assertEquals(5, values.get(0).value());
        assertEquals(5, values.get(1).value());

        Mockito.verify(casterHook, Mockito.times(1)).onEffectValueCast(Mockito.eq(casterBuff), Mockito.any());

        Mockito.verify(casterHook, Mockito.times(1)).onEffectValueTarget(casterBuff, values.get(0));
        Mockito.verify(targetHook, Mockito.times(1)).onEffectValueTarget(targetBuff, values.get(1));
    }

    @Test
    void forEachTargetsWithMaximizeOnOneTarget() throws Exception {
        Fight fight = createFight(true);

        Fighter caster = player.fighter();
        Fighter target = other.fighter();

        Buff targetBuff = new Buff(Mockito.mock(SpellEffect.class), Mockito.mock(Spell.class), target, target, new BuffHook() {
            @Override
            public void onEffectValueTarget(Buff buff, EffectValue value) {
                value.maximize();
            }
        });
        target.buffs().add(targetBuff);

        SpellEffect effect = Mockito.mock(SpellEffect.class);

        Mockito.when(effect.min()).thenReturn(0);
        Mockito.when(effect.max()).thenReturn(10000);

        List<EffectValue> values = new ArrayList<>();
        List<FighterData> fighters = new ArrayList<>();

        EffectValue.forEachTargets(effect, caster, Arrays.asList(caster, target), ((fighter, value) -> {
            values.add(value);
            fighters.add(fighter);
        }));

        assertIterableEquals(Arrays.asList(caster, target), fighters);
        assertCount(2, values);
        assertBetween(0, 9999, values.get(0).value()); // Exclude 10 000
        assertEquals(10000, values.get(1).value());
    }
}
