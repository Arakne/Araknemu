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

package fr.quatrevieux.araknemu.game.fight.castable.effect.handler.armor;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.castable.CastScope;
import fr.quatrevieux.araknemu.game.fight.castable.FightCastScope;
import fr.quatrevieux.araknemu.game.fight.castable.effect.Element;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buff;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.damage.Damage;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.damage.ReflectedDamage;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.spell.SpellConstraints;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import fr.quatrevieux.araknemu.game.spell.effect.area.CellArea;
import fr.quatrevieux.araknemu.game.spell.effect.target.SpellEffectTarget;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HealOrMultiplyDamageHandlerTest extends FightBaseCase {
    private Fight fight;
    private PlayerFighter caster;
    private PlayerFighter target;
    private HealOrMultiplyDamageHandler handler;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        fight = createFight();
        fight.nextState();

        caster = player.fighter();
        target = other.fighter();

        target.move(fight.map().get(123));

        handler = new HealOrMultiplyDamageHandler();

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

        FightCastScope scope = makeCastScope(caster, spell, effect, target.cell());

        assertThrows(UnsupportedOperationException.class, () -> handler.handle(scope, scope.effects().get(0)));
    }

    @Test
    void buff() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(effect.duration()).thenReturn(5);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        FightCastScope scope = makeCastScope(caster, spell, effect, target.cell());
        handler.buff(scope, scope.effects().get(0));

        Optional<Buff> found = target.buffs().stream().filter(buff -> buff.effect().equals(effect)).findFirst();

        assertTrue(found.isPresent());
        assertEquals(caster, found.get().caster());
        assertEquals(target, found.get().target());
        assertEquals(effect, found.get().effect());
        assertEquals(spell, found.get().action());
        assertEquals(handler, found.get().hook());
        assertEquals(5, found.get().remainingTurns());
    }

    @Test
    void onDamageWithMultiplyEffect() {
        SpellEffect returnEffect = Mockito.mock(SpellEffect.class);

        Mockito.when(returnEffect.min()).thenReturn(2);
        Mockito.when(returnEffect.max()).thenReturn(1);
        Mockito.when(returnEffect.special()).thenReturn(0);

        Buff buff = new Buff(returnEffect, Mockito.mock(Spell.class), caster, caster, handler);

        Damage damage = new Damage(20, Element.NEUTRAL);

        handler.onDirectDamage(buff, caster, damage);

        assertEquals(40, damage.value());
    }

    @Test
    void onDamageWithHealEffect() {
        SpellEffect returnEffect = Mockito.mock(SpellEffect.class);

        Mockito.when(returnEffect.min()).thenReturn(2);
        Mockito.when(returnEffect.max()).thenReturn(1);
        Mockito.when(returnEffect.special()).thenReturn(100);

        Buff buff = new Buff(returnEffect, Mockito.mock(Spell.class), caster, caster, handler);

        Damage damage = new Damage(20, Element.NEUTRAL);

        handler.onDirectDamage(buff, caster, damage);

        assertEquals(-20, damage.value());
    }

    @Test
    void onReflectedDamageWithMultiplyEffect() {
        SpellEffect returnEffect = Mockito.mock(SpellEffect.class);

        Mockito.when(returnEffect.min()).thenReturn(2);
        Mockito.when(returnEffect.max()).thenReturn(1);
        Mockito.when(returnEffect.special()).thenReturn(0);

        Buff buff = new Buff(returnEffect, Mockito.mock(Spell.class), caster, caster, handler);

        ReflectedDamage damage = new ReflectedDamage(new Damage(20, Element.NEUTRAL).reflect(10), caster);

        handler.onReflectedDamage(buff, damage);

        assertEquals(20, damage.value());
    }

    @Test
    void onReflectedDamageWithHealEffect() {
        SpellEffect returnEffect = Mockito.mock(SpellEffect.class);

        Mockito.when(returnEffect.min()).thenReturn(2);
        Mockito.when(returnEffect.max()).thenReturn(1);
        Mockito.when(returnEffect.special()).thenReturn(100);

        Buff buff = new Buff(returnEffect, Mockito.mock(Spell.class), caster, caster, handler);

        ReflectedDamage damage = new ReflectedDamage(new Damage(20, Element.NEUTRAL).reflect(10), caster);

        handler.onReflectedDamage(buff, damage);

        assertEquals(-10, damage.value());
    }

    @Test
    void onDamageWithRandom() {
        SpellEffect returnEffect = Mockito.mock(SpellEffect.class);

        Mockito.when(returnEffect.min()).thenReturn(2);
        Mockito.when(returnEffect.max()).thenReturn(1);
        Mockito.when(returnEffect.special()).thenReturn(20);

        Buff buff = new Buff(returnEffect, Mockito.mock(Spell.class), caster, caster, handler);

        int healCount = 0;
        int damageCount = 0;

        for (int i = 0; i < 1000; ++i) {
            Damage damage = new Damage(20, Element.NEUTRAL);
            handler.onDirectDamage(buff, caster, damage);

            if (damage.value() == 40) {
                ++damageCount;
            } else if (damage.value() == -20) {
                ++healCount;
            }
        }

        assertEquals(1000, healCount + damageCount);
        assertBetween(150, 250, healCount);
        assertBetween(750, 850, damageCount);
    }
}