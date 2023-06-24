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

package fr.quatrevieux.araknemu.game.fight.castable.effect.handler.heal;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.data.value.EffectArea;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.castable.FightCastScope;
import fr.quatrevieux.araknemu.game.fight.castable.effect.Element;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buff;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.BuffHook;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.damage.Damage;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.damage.DamageApplier;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.spell.SpellConstraints;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import fr.quatrevieux.araknemu.game.spell.effect.area.CellArea;
import fr.quatrevieux.araknemu.game.spell.effect.area.CircleArea;
import fr.quatrevieux.araknemu.game.spell.effect.target.SpellEffectTarget;
import fr.quatrevieux.araknemu.network.game.out.fight.action.ActionEffect;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HealOnAttackHandlerTest extends FightBaseCase {
    private Fight fight;
    private PlayerFighter caster;
    private PlayerFighter target;
    private HealOnAttackHandler handler;
    private int lastCasterLife;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        fight = createFight();
        fight.nextState();

        caster = player.fighter();
        target = other.fighter();

        target.move(fight.map().get(123));
        caster.life().alter(caster, -30);
        lastCasterLife = caster.life().current();

        handler = new HealOnAttackHandler();

        player.properties().characteristics().base().set(Characteristic.INTELLIGENCE, 0);

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

        FightCastScope scope = makeCastScope(caster, spell, effect, caster.cell());
        assertThrows(UnsupportedOperationException.class, () -> handler.handle(scope, scope.effects().get(0)));
    }

    @Test
    void buffWillAddBuffToList() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.min()).thenReturn(10);
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
    void buffWithAreaMultipleFighters() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.min()).thenReturn(10);
        Mockito.when(effect.area()).thenReturn(new CircleArea(new EffectArea(EffectArea.Type.CIRCLE, 20)));
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        FightCastScope scope = makeCastScope(caster, spell, effect, fight.map().get(122));
        handler.buff(scope, scope.effects().get(0));

        assertTrue(caster.buffs().stream().anyMatch(buff -> buff.effect().equals(effect)));
        assertTrue(target.buffs().stream().anyMatch(buff -> buff.effect().equals(effect)));
    }

    @Test
    void onDirectDamage() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.special()).thenReturn(100);
        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        FightCastScope scope = makeCastScope(caster, spell, effect, target.cell());
        handler.buff(scope, scope.effects().get(0));

        new DamageApplier(Element.WATER, fight).applyFixed(caster, 10, target);

        assertEquals(10, computeHeal());

        requestStack.assertOne(ActionEffect.alterLifePoints(target, caster, computeHeal()));
    }

    @Test
    void onDirectDamageTransformedToHealShouldBeIgnored() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.special()).thenReturn(100);
        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        target.buffs().add(new Buff(effect, spell, target, target, new BuffHook() {
            @Override
            public void onDamage(Buff buff, Damage value) {
                value.multiply(-1); // Transform damage to heal
            }
        }));

        FightCastScope scope = makeCastScope(caster, spell, effect, target.cell());
        handler.buff(scope, scope.effects().get(0));

        new DamageApplier(Element.WATER, fight).applyFixed(caster, 10, target);

        assertEquals(0, computeHeal());
    }

    @Test
    void onDirectDamageNot100Percent() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.special()).thenReturn(50);
        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        FightCastScope scope = makeCastScope(caster, spell, effect, target.cell());
        handler.buff(scope, scope.effects().get(0));

        new DamageApplier(Element.WATER, fight).applyFixed(caster, 10, target);

        assertEquals(5, computeHeal());

        requestStack.assertOne(ActionEffect.alterLifePoints(target, caster, computeHeal()));
    }

    @Test
    void onIndirectDamageShouldBeIgnored() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.special()).thenReturn(100);
        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        FightCastScope scope = makeCastScope(caster, spell, effect, target.cell());
        handler.buff(scope, scope.effects().get(0));

        new DamageApplier(Element.WATER, fight).applyIndirectFixed(caster, 10, target);

        assertEquals(0, computeHeal());
    }

    private int computeHeal() {
        return caster.life().current() - lastCasterLife;
    }
}
