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

package fr.quatrevieux.araknemu.game.fight.castable.effect.handler.damage;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.castable.effect.Element;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buff;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.BuffHook;
import fr.quatrevieux.araknemu.game.fight.fighter.ActiveFighter;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.PassiveFighter;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import fr.quatrevieux.araknemu.network.game.out.fight.action.ActionEffect;
import org.checkerframework.checker.index.qual.Positive;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DamageApplierTest extends FightBaseCase {
    private PlayerFighter caster;
    private PlayerFighter target;
    private Fight fight;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        fight = createFight();
        fight.nextState();

        caster = player.fighter();
        target = other.fighter();

        requestStack.clear();
    }

    @Test
    void applyFixedWithoutBoost() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);

        Mockito.when(effect.min()).thenReturn(10);

        DamageApplier applier = new DamageApplier(Element.AIR, fight);

        int value = applier.apply(caster, effect, target);

        assertEquals(-10, value);
        assertEquals(10, target.life().max() - target.life().current());

        requestStack.assertLast(ActionEffect.alterLifePoints(caster, target, -10));
    }

    @Test
    void applyRandomWithoutBoost() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);

        Mockito.when(effect.min()).thenReturn(10);
        Mockito.when(effect.max()).thenReturn(15);

        DamageApplier applier = new DamageApplier(Element.AIR, fight);

        int value = applier.apply(caster, effect, target);

        assertBetween(-15, -10, value);
        assertEquals(value, target.life().current() - target.life().max());

        requestStack.assertLast(ActionEffect.alterLifePoints(caster, target, value));
    }

    @Test
    void applyWithBoost() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);

        Mockito.when(effect.min()).thenReturn(10);

        DamageApplier applier = new DamageApplier(Element.AIR, fight);

        player.properties().characteristics().base().set(Characteristic.AGILITY, 50);
        player.properties().characteristics().base().set(Characteristic.PERCENT_DAMAGE, 25);
        player.properties().characteristics().base().set(Characteristic.FIXED_DAMAGE, 10);

        int value = applier.apply(caster, effect, target);

        assertEquals(-27, value);
    }

    @Test
    void applyWithResistance() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);

        Mockito.when(effect.min()).thenReturn(10);

        DamageApplier applier = new DamageApplier(Element.AIR, fight);

        other.properties().characteristics().base().set(Characteristic.RESISTANCE_PERCENT_AIR, 25);
        other.properties().characteristics().base().set(Characteristic.RESISTANCE_AIR, 5);

        int value = applier.apply(caster, effect, target);

        assertEquals(-2, value);
    }

    @Test
    void applyWithTooHighResistance() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);

        Mockito.when(effect.min()).thenReturn(10);

        DamageApplier applier = new DamageApplier(Element.AIR, fight);

        other.properties().characteristics().base().set(Characteristic.RESISTANCE_AIR, 100);

        int value = applier.apply(caster, effect, target);

        assertEquals(0, value);
    }

    @Test
    void applyWithValueHigherThanTargetLife() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);

        Mockito.when(effect.min()).thenReturn(1000);

        DamageApplier applier = new DamageApplier(Element.AIR, fight);

        int value = applier.apply(caster, effect, target);

        assertEquals(-50, value);
        assertTrue(target.dead());

        requestStack.assertLast(ActionEffect.fighterDie(caster, target));
    }

    @Test
    void applyWithReduceBuff() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);

        Mockito.when(effect.min()).thenReturn(10);

        DamageApplier applier = new DamageApplier(Element.AIR, fight);

        target.buffs().add(
            new Buff(Mockito.mock(SpellEffect.class), Mockito.mock(Spell.class), target, target, new BuffHook() {
                @Override
                public void onDamage(Buff buff, Damage value) {
                    value.reduce(7);
                }
            })
        );

        int value = applier.apply(caster, effect, target);

        assertEquals(-3, value);

        requestStack.assertOne(ActionEffect.alterLifePoints(caster, target, -3));
        requestStack.assertOne(ActionEffect.reducedDamage(target, 7));
    }

    @Test
    void applyDirectDamageShouldCallBuffHook() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);

        Mockito.when(effect.min()).thenReturn(10);

        DamageApplier applier = new DamageApplier(Element.AIR, fight);

        AtomicReference<Buff> calledBuff = new AtomicReference<>();
        AtomicReference<PassiveFighter> calledCaster = new AtomicReference<>();
        AtomicReference<Damage> calledDamage = new AtomicReference<>();
        AtomicReference<Buff> appliedDamageBuff = new AtomicReference<>();
        AtomicReference<PassiveFighter> appliedDamageCaster = new AtomicReference<>();
        AtomicInteger appliedDamageValue = new AtomicInteger();

        Buff buff = new Buff(Mockito.mock(SpellEffect.class), Mockito.mock(Spell.class), target, target, new BuffHook() {
            @Override
            public void onDirectDamage(Buff buff, Fighter caster, Damage value) {
                calledBuff.set(buff);
                calledCaster.set(caster);
                calledDamage.set(value);
            }

            @Override
            public void onDirectDamageApplied(Buff buff, Fighter caster, @Positive int damage) {
                appliedDamageBuff.set(buff);
                appliedDamageCaster.set(caster);
                appliedDamageValue.set(damage);
            }
        });

        target.buffs().add(buff);

        int value = applier.apply(caster, effect, target);

        assertEquals(-10, value);

        assertSame(buff, calledBuff.get());
        assertSame(caster, calledCaster.get());
        assertEquals(10, calledDamage.get().value());

        assertSame(buff, appliedDamageBuff.get());
        assertSame(caster, appliedDamageCaster.get());
        assertEquals(10, appliedDamageValue.get());
    }

    @Test
    void applyBuffDamageShouldCallBuffHook() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);

        Mockito.when(effect.min()).thenReturn(10);

        DamageApplier applier = new DamageApplier(Element.AIR, fight);

        AtomicReference<Buff> calledBuff = new AtomicReference<>();
        AtomicReference<Buff> calledPoison = new AtomicReference<>();
        AtomicReference<Damage> calledDamage = new AtomicReference<>();
        AtomicBoolean appliedDamageHookCalled = new AtomicBoolean();

        Buff buff = new Buff(Mockito.mock(SpellEffect.class), Mockito.mock(Spell.class), target, target, new BuffHook() {
            @Override
            public void onBuffDamage(Buff buff, Buff poison, Damage value) {
                calledBuff.set(buff);
                calledPoison.set(poison);
                calledDamage.set(value);
            }

            @Override
            public void onDirectDamageApplied(Buff buff, Fighter caster, @Positive int damage) {
                appliedDamageHookCalled.set(true);
            }
        });

        target.buffs().add(buff);

        Buff toApply = new Buff(effect, Mockito.mock(Spell.class), caster, target, Mockito.mock(BuffHook.class));
        int value = applier.apply(toApply);

        assertEquals(-10, value);

        assertSame(buff, calledBuff.get());
        assertSame(toApply, calledPoison.get());
        assertEquals(10, calledDamage.get().value());
        assertFalse(appliedDamageHookCalled.get());
    }

    @Test
    void applyWithCounterDamageCharacteristic() {
        target.characteristics().alter(Characteristic.COUNTER_DAMAGE, 5);
        SpellEffect effect = Mockito.mock(SpellEffect.class);

        AtomicReference<Buff> calledBuff = new AtomicReference<>();
        AtomicReference<ReflectedDamage> calledReflectedDamage = new AtomicReference<>();

        Buff buff = new Buff(Mockito.mock(SpellEffect.class), Mockito.mock(Spell.class), target, target, new BuffHook() {
            @Override
            public void onReflectedDamage(Buff buff, ReflectedDamage damage) {
                calledBuff.set(buff);
                calledReflectedDamage.set(damage);
            }
        });

        caster.buffs().add(buff);

        Mockito.when(effect.min()).thenReturn(10);

        DamageApplier applier = new DamageApplier(Element.AIR, fight);
        requestStack.clear();

        assertEquals(-10, applier.apply(caster, effect, target));
        assertEquals(-10, target.life().current() - target.life().max());
        assertEquals(-5, caster.life().current() - caster.life().max());

        assertSame(buff, calledBuff.get());
        assertEquals(5, calledReflectedDamage.get().value());
        assertSame(caster, calledReflectedDamage.get().target());

        requestStack.assertAll(
            ActionEffect.alterLifePoints(caster, target, -10),
            ActionEffect.reflectedDamage(target, 5),
            ActionEffect.alterLifePoints(target, caster, -5)
        );
    }

    @Test
    void applyWithCounterDamageShouldNotExceedHalfOfDamage() {
        target.characteristics().alter(Characteristic.COUNTER_DAMAGE, 10);
        SpellEffect effect = Mockito.mock(SpellEffect.class);

        Mockito.when(effect.min()).thenReturn(10);

        DamageApplier applier = new DamageApplier(Element.AIR, fight);
        requestStack.clear();

        assertEquals(-10, applier.apply(caster, effect, target));
        assertEquals(-10, target.life().current() - target.life().max());
        assertEquals(-5, caster.life().current() - caster.life().max());

        requestStack.assertAll(
            ActionEffect.alterLifePoints(caster, target, -10),
            ActionEffect.reflectedDamage(target, 5),
            ActionEffect.alterLifePoints(target, caster, -5)
        );
    }

    @Test
    void applyWithCounterDamageShouldTakeResistanceInAccount() {
        target.characteristics().alter(Characteristic.COUNTER_DAMAGE, 10);
        caster.characteristics().alter(Characteristic.RESISTANCE_AIR, 2);
        caster.characteristics().alter(Characteristic.RESISTANCE_PERCENT_AIR, 10);
        SpellEffect effect = Mockito.mock(SpellEffect.class);

        Mockito.when(effect.min()).thenReturn(20);

        DamageApplier applier = new DamageApplier(Element.AIR, fight);
        requestStack.clear();

        assertEquals(-20, applier.apply(caster, effect, target));
        assertEquals(-20, target.life().current() - target.life().max());
        assertEquals(-7, caster.life().current() - caster.life().max());

        requestStack.assertAll(
            ActionEffect.alterLifePoints(caster, target, -20),
            ActionEffect.reflectedDamage(target, 7),
            ActionEffect.alterLifePoints(target, caster, -7)
        );
    }

    @Test
    void applyWithCounterDamageChangeTarget() throws SQLException {
        PlayerFighter newTarget = makePlayerFighter(makeSimpleGamePlayer(11));
        fight.team(0).join(newTarget);
        newTarget.joinFight(fight, fight.map().get(150));
        newTarget.init();

        target.characteristics().alter(Characteristic.COUNTER_DAMAGE, 5);
        SpellEffect effect = Mockito.mock(SpellEffect.class);

        Buff buff = new Buff(Mockito.mock(SpellEffect.class), Mockito.mock(Spell.class), caster, caster, new BuffHook() {
            @Override
            public void onReflectedDamage(Buff buff, ReflectedDamage damage) {
                damage.changeTarget(newTarget);
            }
        });

        caster.buffs().add(buff);

        Mockito.when(effect.min()).thenReturn(10);

        DamageApplier applier = new DamageApplier(Element.AIR, fight);
        requestStack.clear();

        assertEquals(-10, applier.apply(caster, effect, target));
        assertEquals(-10, target.life().current() - target.life().max());
        assertEquals(caster.life().current(), caster.life().max());
        assertEquals(-5, newTarget.life().current() - newTarget.life().max());

        requestStack.assertAll(
            ActionEffect.alterLifePoints(caster, target, -10),
            ActionEffect.reflectedDamage(target, 5),
            ActionEffect.alterLifePoints(target, newTarget, -5)
        );
    }

    @Test
    void applyWithCounterDamageShouldIgnoreSelfTarget() {
        caster.characteristics().alter(Characteristic.COUNTER_DAMAGE, 10);
        SpellEffect effect = Mockito.mock(SpellEffect.class);

        Mockito.when(effect.min()).thenReturn(10);

        DamageApplier applier = new DamageApplier(Element.AIR, fight);
        requestStack.clear();

        assertEquals(-10, applier.apply(caster, effect, caster));
        assertEquals(-10, caster.life().current() - caster.life().max());

        requestStack.assertAll(
            ActionEffect.alterLifePoints(caster, caster, -10)
        );
    }

    @Test
    void applyWithCounterDamageNoDamageShouldBeIgnored() {
        target.characteristics().alter(Characteristic.COUNTER_DAMAGE, 5);
        target.characteristics().alter(Characteristic.RESISTANCE_AIR, 10);
        SpellEffect effect = Mockito.mock(SpellEffect.class);

        BuffHook hook = Mockito.mock(BuffHook.class);
        Buff buff = new Buff(Mockito.mock(SpellEffect.class), Mockito.mock(Spell.class), target, target, hook);

        caster.buffs().add(buff);

        Mockito.when(effect.min()).thenReturn(10);

        DamageApplier applier = new DamageApplier(Element.AIR, fight);
        requestStack.clear();

        assertEquals(0, applier.apply(caster, effect, target));
        assertEquals(0, target.life().current() - target.life().max());
        assertEquals(0, caster.life().current() - caster.life().max());

        Mockito.verify(hook, Mockito.never()).onReflectedDamage(Mockito.any(), Mockito.any());

        requestStack.assertAll(
            ActionEffect.alterLifePoints(caster, target, 0)
        );
    }

    @Test
    void applyWithCounterDamageAndNegativeMultiplierShouldHealTarget() {
        target.characteristics().alter(Characteristic.COUNTER_DAMAGE, 5);
        caster.life().alter(caster, -10);
        SpellEffect effect = Mockito.mock(SpellEffect.class);

        Buff buff = new Buff(Mockito.mock(SpellEffect.class), Mockito.mock(Spell.class), target, target, new BuffHook() {
            @Override
            public void onReflectedDamage(Buff buff, ReflectedDamage damage) {
                damage.multiply(-1);
            }
        });

        caster.buffs().add(buff);

        Mockito.when(effect.min()).thenReturn(10);

        DamageApplier applier = new DamageApplier(Element.AIR, fight);
        requestStack.clear();

        assertEquals(-10, applier.apply(caster, effect, target));
        assertEquals(-10, target.life().current() - target.life().max());
        assertEquals(-5, caster.life().current() - caster.life().max());

        requestStack.assertAll(
            ActionEffect.alterLifePoints(caster, target, -10),
            ActionEffect.reflectedDamage(target, 5),
            ActionEffect.alterLifePoints(target, caster, 5)
        );
    }

    @Test
    void applyWithNegativeMultiplierShouldHealTargetAndNotCallDirectDamageAppliedHook() {
        target.life().alter(target, -20);
        SpellEffect effect = Mockito.mock(SpellEffect.class);

        AtomicBoolean appliedDamageHookCalled = new AtomicBoolean();

        Buff buff = new Buff(Mockito.mock(SpellEffect.class), Mockito.mock(Spell.class), target, target, new BuffHook() {
            @Override
            public void onDirectDamage(Buff buff, Fighter caster, Damage value) {
                value.multiply(-1);
            }

            @Override
            public void onDirectDamageApplied(Buff buff, Fighter caster, @Positive int damage) {
                appliedDamageHookCalled.set(true);
            }
        });

        target.buffs().add(buff);

        Mockito.when(effect.min()).thenReturn(10);

        DamageApplier applier = new DamageApplier(Element.AIR, fight);
        requestStack.clear();

        assertEquals(10, applier.apply(caster, effect, target));
        assertEquals(-10, target.life().current() - target.life().max());
        assertFalse(appliedDamageHookCalled.get());

        requestStack.assertAll(ActionEffect.alterLifePoints(caster, target, 10));
    }

    @Test
    void applyFixedBase() {
        DamageApplier applier = new DamageApplier(Element.EARTH, fight);

        AtomicReference<Buff> calledBuff = new AtomicReference<>();
        AtomicReference<PassiveFighter> calledCaster = new AtomicReference<>();
        AtomicReference<Damage> calledDamage = new AtomicReference<>();
        AtomicReference<Buff> appliedDamageBuff = new AtomicReference<>();
        AtomicReference<PassiveFighter> appliedDamageCaster = new AtomicReference<>();
        AtomicInteger appliedDamageValue = new AtomicInteger();

        Buff buff = new Buff(Mockito.mock(SpellEffect.class), Mockito.mock(Spell.class), target, target, new BuffHook() {
            @Override
            public void onDirectDamage(Buff buff, Fighter caster, Damage value) {
                calledBuff.set(buff);
                calledCaster.set(caster);
                calledDamage.set(value);
            }

            @Override
            public void onDirectDamageApplied(Buff buff, Fighter caster, @Positive int damage) {
                appliedDamageBuff.set(buff);
                appliedDamageCaster.set(caster);
                appliedDamageValue.set(damage);
            }
        });

        target.buffs().add(buff);
        requestStack.clear();

        assertEquals(-10, applier.applyFixed(caster, 10, target));
        assertEquals(-10, target.life().current() - target.life().max());

        requestStack.assertAll(
            ActionEffect.alterLifePoints(caster, target, -10)
        );

        assertSame(buff, calledBuff.get());
        assertSame(caster, calledCaster.get());
        assertEquals(10, calledDamage.get().value());

        assertSame(buff, appliedDamageBuff.get());
        assertSame(caster, appliedDamageCaster.get());
        assertEquals(10, appliedDamageValue.get());
    }

    @Test
    void applyFixedShouldIgnoreBoost() {
        caster.characteristics().alter(Characteristic.STRENGTH, 100);
        caster.characteristics().alter(Characteristic.FIXED_DAMAGE, 100);

        DamageApplier applier = new DamageApplier(Element.EARTH, fight);
        requestStack.clear();

        assertEquals(-10, applier.applyFixed(caster, 10, target));
        assertEquals(-10, target.life().current() - target.life().max());

        requestStack.assertAll(
            ActionEffect.alterLifePoints(caster, target, -10)
        );
    }

    @Test
    void applyFixedWithCounterDamageCharacteristic() {
        target.characteristics().alter(Characteristic.COUNTER_DAMAGE, 5);
        SpellEffect effect = Mockito.mock(SpellEffect.class);

        AtomicReference<Buff> calledBuff = new AtomicReference<>();
        AtomicReference<ReflectedDamage> calledReflectedDamage = new AtomicReference<>();

        Buff buff = new Buff(Mockito.mock(SpellEffect.class), Mockito.mock(Spell.class), target, target, new BuffHook() {
            @Override
            public void onReflectedDamage(Buff buff, ReflectedDamage damage) {
                calledBuff.set(buff);
                calledReflectedDamage.set(damage);
            }
        });

        caster.buffs().add(buff);

        Mockito.when(effect.min()).thenReturn(10);

        DamageApplier applier = new DamageApplier(Element.AIR, fight);
        requestStack.clear();

        assertEquals(-10, applier.applyFixed(caster, 10, target));
        assertEquals(-10, target.life().current() - target.life().max());
        assertEquals(-5, caster.life().current() - caster.life().max());

        assertSame(buff, calledBuff.get());
        assertEquals(5, calledReflectedDamage.get().value());
        assertSame(caster, calledReflectedDamage.get().target());

        requestStack.assertAll(
            ActionEffect.alterLifePoints(caster, target, -10),
            ActionEffect.reflectedDamage(target, 5),
            ActionEffect.alterLifePoints(target, caster, -5)
        );
    }

    @Test
    void applyFixedWithResistance() {
        target.characteristics().alter(Characteristic.RESISTANCE_EARTH, 3);
        target.characteristics().alter(Characteristic.RESISTANCE_PERCENT_EARTH, 10);

        DamageApplier applier = new DamageApplier(Element.EARTH, fight);
        requestStack.clear();

        assertEquals(-6, applier.applyFixed(caster, 10, target));
        assertEquals(-6, target.life().current() - target.life().max());

        requestStack.assertAll(
            ActionEffect.alterLifePoints(caster, target, -6)
        );
    }

    @Test
    void applyIndirectFixedBase() {
        DamageApplier applier = new DamageApplier(Element.EARTH, fight);

        AtomicReference<Buff> calledBuff = new AtomicReference<>();
        AtomicReference<PassiveFighter> calledCaster = new AtomicReference<>();
        AtomicReference<Damage> calledDamage = new AtomicReference<>();
        AtomicBoolean appliedDamageHookCalled = new AtomicBoolean();

        Buff buff = new Buff(Mockito.mock(SpellEffect.class), Mockito.mock(Spell.class), target, target, new BuffHook() {
            @Override
            public void onIndirectDamage(Buff buff, Fighter caster, Damage value) {
                calledBuff.set(buff);
                calledCaster.set(caster);
                calledDamage.set(value);
            }

            @Override
            public void onDirectDamageApplied(Buff buff, Fighter caster, @Positive int damage) {
                appliedDamageHookCalled.set(true);
            }
        });

        target.buffs().add(buff);
        requestStack.clear();

        assertEquals(-10, applier.applyIndirectFixed(caster, 10, target));
        assertEquals(-10, target.life().current() - target.life().max());

        requestStack.assertAll(
            ActionEffect.alterLifePoints(caster, target, -10)
        );

        assertSame(buff, calledBuff.get());
        assertSame(caster, calledCaster.get());
        assertEquals(10, calledDamage.get().value());

        assertFalse(appliedDamageHookCalled.get());
    }

    @Test
    void applyIndirectFixedShouldIgnoreBoost() {
        caster.characteristics().alter(Characteristic.STRENGTH, 100);
        caster.characteristics().alter(Characteristic.FIXED_DAMAGE, 100);

        DamageApplier applier = new DamageApplier(Element.EARTH, fight);
        requestStack.clear();

        assertEquals(-10, applier.applyIndirectFixed(caster, 10, target));
        assertEquals(-10, target.life().current() - target.life().max());

        requestStack.assertAll(
            ActionEffect.alterLifePoints(caster, target, -10)
        );
    }

    @Test
    void applyIndirectFixedWithCounterDamageCharacteristicShouldBeIgnored() {
        target.characteristics().alter(Characteristic.COUNTER_DAMAGE, 5);
        SpellEffect effect = Mockito.mock(SpellEffect.class);

        BuffHook hook = Mockito.mock(BuffHook.class);
        Buff buff = new Buff(Mockito.mock(SpellEffect.class), Mockito.mock(Spell.class), target, target, hook);

        caster.buffs().add(buff);

        Mockito.when(effect.min()).thenReturn(10);

        DamageApplier applier = new DamageApplier(Element.AIR, fight);
        requestStack.clear();

        assertEquals(-10, applier.applyIndirectFixed(caster, 10, target));
        assertEquals(-10, target.life().current() - target.life().max());
        assertEquals(caster.life().current(), caster.life().max());

        Mockito.verify(hook, Mockito.never()).onReflectedDamage(Mockito.any(), Mockito.any());

        requestStack.assertAll(
            ActionEffect.alterLifePoints(caster, target, -10)
        );
    }

    @Test
    void applyIndirectFixedWithResistance() {
        target.characteristics().alter(Characteristic.RESISTANCE_EARTH, 3);
        target.characteristics().alter(Characteristic.RESISTANCE_PERCENT_EARTH, 10);

        DamageApplier applier = new DamageApplier(Element.EARTH, fight);
        requestStack.clear();

        assertEquals(-6, applier.applyIndirectFixed(caster, 10, target));
        assertEquals(-6, target.life().current() - target.life().max());

        requestStack.assertAll(
            ActionEffect.alterLifePoints(caster, target, -6)
        );
    }

    @Test
    void applyFixedBuffDamage() {
        DamageApplier applier = new DamageApplier(Element.AIR, fight);

        AtomicReference<Buff> calledBuff = new AtomicReference<>();
        AtomicReference<Buff> calledPoison = new AtomicReference<>();
        AtomicReference<Damage> calledDamage = new AtomicReference<>();

        Buff buff = new Buff(Mockito.mock(SpellEffect.class), Mockito.mock(Spell.class), target, target, new BuffHook() {
            @Override
            public void onBuffDamage(Buff buff, Buff poison, Damage value) {
                calledBuff.set(buff);
                calledPoison.set(poison);
                calledDamage.set(value);
            }
        });

        target.buffs().add(buff);

        Buff toApply = new Buff(Mockito.mock(SpellEffect.class), Mockito.mock(Spell.class), caster, target, Mockito.mock(BuffHook.class));
        int value = applier.applyFixed(toApply, 10);

        assertEquals(-10, value);
        assertEquals(-10, target.life().current() - target.life().max());

        assertSame(buff, calledBuff.get());
        assertSame(toApply, calledPoison.get());
        assertEquals(10, calledDamage.get().value());
    }

    @Test
    void applyFixedBuffDamageWithResistance() {
        target.characteristics().alter(Characteristic.RESISTANCE_PERCENT_AIR, 50);
        target.characteristics().alter(Characteristic.RESISTANCE_AIR, 3);

        DamageApplier applier = new DamageApplier(Element.AIR, fight);

        Buff toApply = new Buff(Mockito.mock(SpellEffect.class), Mockito.mock(Spell.class), caster, target, Mockito.mock(BuffHook.class));
        int value = applier.applyFixed(toApply, 10);

        assertEquals(-2, value);
        assertEquals(-2, target.life().current() - target.life().max());
    }

    @Test
    void applyShouldCallOnCastDamageOnCaster() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);

        Mockito.when(effect.min()).thenReturn(10);

        DamageApplier applier = new DamageApplier(Element.AIR, fight);

        BuffHook hook = Mockito.mock(BuffHook.class);
        Buff buff = new Buff(Mockito.mock(SpellEffect.class), Mockito.mock(Spell.class), target, target, hook);

        caster.buffs().add(buff);

        int value = applier.apply(caster, effect, target);

        assertEquals(-10, value);

        Mockito.verify(hook, Mockito.times(1)).onCastDamage(Mockito.eq(buff), Mockito.argThat(damage -> damage.value() == 10), Mockito.eq(target));
    }

    @Test
    void applyFixedShouldCallOnCastDamageOnCaster() {
        DamageApplier applier = new DamageApplier(Element.AIR, fight);

        BuffHook hook = Mockito.mock(BuffHook.class);
        Buff buff = new Buff(Mockito.mock(SpellEffect.class), Mockito.mock(Spell.class), target, target, hook);

        caster.buffs().add(buff);

        int value = applier.applyFixed(caster, 10, target);

        assertEquals(-10, value);

        Mockito.verify(hook, Mockito.times(1)).onCastDamage(Mockito.eq(buff), Mockito.argThat(damage -> damage.value() == 10), Mockito.eq(target));
    }

    @Test
    void applyBuffShouldCallOnCastDamageOnCaster() {
        DamageApplier applier = new DamageApplier(Element.AIR, fight);

        BuffHook hook = Mockito.mock(BuffHook.class);
        Buff buff = new Buff(Mockito.mock(SpellEffect.class), Mockito.mock(Spell.class), target, target, hook);

        caster.buffs().add(buff);

        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Mockito.when(effect.min()).thenReturn(10);
        Buff toApply = new Buff(effect, Mockito.mock(Spell.class), caster, target, Mockito.mock(BuffHook.class));

        int value = applier.apply(toApply);

        assertEquals(-10, value);

        Mockito.verify(hook, Mockito.times(1)).onCastDamage(Mockito.eq(buff), Mockito.argThat(damage -> damage.value() == 10), Mockito.eq(target));
    }

    @Test
    void applyFixedBuffShouldCallOnCastDamageOnCaster() {
        DamageApplier applier = new DamageApplier(Element.AIR, fight);

        BuffHook hook = Mockito.mock(BuffHook.class);
        Buff buff = new Buff(Mockito.mock(SpellEffect.class), Mockito.mock(Spell.class), target, target, hook);

        caster.buffs().add(buff);

        Buff toApply = new Buff(Mockito.mock(SpellEffect.class), Mockito.mock(Spell.class), caster, target, Mockito.mock(BuffHook.class));

        int value = applier.applyFixed(toApply, 10);

        assertEquals(-10, value);

        Mockito.verify(hook, Mockito.times(1)).onCastDamage(Mockito.eq(buff), Mockito.argThat(damage -> damage.value() == 10), Mockito.eq(target));
    }
}
