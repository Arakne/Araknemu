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
 * Copyright (c) 2017-2024 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.castable.effect.hook;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.data.value.EffectArea;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.castable.FightCastScope;
import fr.quatrevieux.araknemu.game.fight.castable.effect.Element;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buff;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.characteristic.AddCharacteristicHandler;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.damage.DamageApplier;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.damage.DamageHandler;
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

import static org.junit.jupiter.api.Assertions.*;

class ApplyOnElementDamageTest extends FightBaseCase {

    private Fight fight;
    private PlayerFighter caster;
    private PlayerFighter target;
    private AddCharacteristicHandler handler;
    private ApplyOnElementDamage hook;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        fight = createFight();
        fight.nextState();

        caster = player.fighter();
        target = other.fighter();

        target.move(fight.map().get(123));

        handler = new AddCharacteristicHandler(fight, Characteristic.FIXED_DAMAGE);
        hook = new ApplyOnElementDamage(Element.AIR);

        requestStack.clear();
    }

    @Test
    void apply() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.effect()).thenReturn(145);
        Mockito.when(effect.min()).thenReturn(10);
        Mockito.when(effect.duration()).thenReturn(5);
        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(new SpellEffectTarget(64));
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        FightCastScope scope = makeCastScope(caster, spell, effect, target.cell());

        assertFalse(hook.apply(handler, scope, scope.effects().get(0)));

        Buff buff = target.buffs().stream().filter(b -> b.effect().effect() == 145).findFirst().get();
        requestStack.assertLast(new AddBuff(buff));
        requestStack.clear();

        new DamageApplier(Element.AIR, fight).applyFixed(caster, 1, target);

        Buff addedBuff = target.buffs().stream().filter(b -> b.effect().effect() == 145 && b.hook() instanceof AddCharacteristicHandler).findFirst().get();

        requestStack.assertAll(
            ActionEffect.alterLifePoints(caster, target, -1),
            ActionEffect.buff(addedBuff, 10),
            new AddBuff(addedBuff)
        );

        assertEquals(5, addedBuff.remainingTurns());
        assertEquals(10, target.characteristics().get(Characteristic.FIXED_DAMAGE));
    }

    @Test
    void applyElementNotMatchingShouldDoNothing() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.effect()).thenReturn(145);
        Mockito.when(effect.min()).thenReturn(10);
        Mockito.when(effect.duration()).thenReturn(5);
        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(new SpellEffectTarget(64));
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        FightCastScope scope = makeCastScope(caster, spell, effect, target.cell());

        assertFalse(hook.apply(handler, scope, scope.effects().get(0)));

        Buff buff = target.buffs().stream().filter(b -> b.effect().effect() == 145).findFirst().get();
        requestStack.assertLast(new AddBuff(buff));
        requestStack.clear();

        new DamageApplier(Element.EARTH, fight).applyFixed(caster, 1, target);

        assertFalse(target.buffs().stream().anyMatch(b -> b.effect().effect() == 145 && b.hook() instanceof AddCharacteristicHandler));

        requestStack.assertAll(
            ActionEffect.alterLifePoints(caster, target, -1)
        );

        assertEquals(0, target.characteristics().get(Characteristic.FIXED_DAMAGE));
    }

    @Test
    void applyArea() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.effect()).thenReturn(145);
        Mockito.when(effect.min()).thenReturn(10);
        Mockito.when(effect.duration()).thenReturn(5);
        Mockito.when(effect.area()).thenReturn(new CircleArea(new EffectArea(EffectArea.Type.CIRCLE, 10)));
        Mockito.when(effect.target()).thenReturn(new SpellEffectTarget(64));
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        FightCastScope scope = makeCastScope(caster, spell, effect, target.cell());
        requestStack.clear();

        assertFalse(hook.apply(handler, scope, scope.effects().get(0)));

        Buff buff1 = target.buffs().stream().filter(b -> b.effect().effect() == 145).findFirst().get();
        Buff buff2 = caster.buffs().stream().filter(b -> b.effect().effect() == 145).findFirst().get();

        requestStack.assertAll(
            new AddBuff(buff1),
            new AddBuff(buff2)
        );
        requestStack.clear();

        new DamageApplier(Element.AIR, fight).applyFixed(caster, 1, target);
        new DamageApplier(Element.AIR, fight).applyFixed(caster, 1, caster);

        assertEquals(10, caster.characteristics().get(Characteristic.FIXED_DAMAGE));
        assertEquals(10, target.characteristics().get(Characteristic.FIXED_DAMAGE));
    }
}
