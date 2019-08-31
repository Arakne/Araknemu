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
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import fr.quatrevieux.araknemu.network.game.out.fight.action.ActionEffect;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
}
