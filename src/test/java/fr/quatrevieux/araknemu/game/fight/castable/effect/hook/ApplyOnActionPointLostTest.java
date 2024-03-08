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

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class ApplyOnActionPointLostTest extends FightBaseCase {

    private Fight fight;
    private PlayerFighter caster;
    private PlayerFighter target;
    private AddCharacteristicHandler handler;
    private ApplyOnActionPointLost hook;

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
        hook = new ApplyOnActionPointLost();

        requestStack.clear();
    }

    @Test
    void apply() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.effect()).thenReturn(96);
        Mockito.when(effect.min()).thenReturn(10);
        Mockito.when(effect.duration()).thenReturn(5);
        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(new SpellEffectTarget(64));
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        FightCastScope scope = makeCastScope(caster, spell, effect, target.cell());

        assertFalse(hook.apply(handler, scope, scope.effects().get(0)));

        Buff buff = target.buffs().stream().filter(b -> b.effect().effect() == 96).findFirst().get();
        requestStack.assertLast(new AddBuff(buff));
        requestStack.clear();

        target.buffs().onCharacteristicAltered(Characteristic.ACTION_POINT, -1);
        assertEquals(10, target.characteristics().get(Characteristic.FIXED_DAMAGE));

        Buff effectBuff = target.buffs().stream().filter(b -> b.effect().effect() == 96 && b.hook().equals(handler)).findFirst().get();
        assertEquals(10, effectBuff.effect().min());
        assertEquals(5, effectBuff.remainingTurns());

        requestStack.assertAll(
            ActionEffect.buff(effectBuff, 10),
            new AddBuff(effectBuff)
        );
    }

    @Test
    void applyMultipleApShouldApplyMultipleTimeEffect() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.effect()).thenReturn(96);
        Mockito.when(effect.min()).thenReturn(10);
        Mockito.when(effect.duration()).thenReturn(5);
        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(new SpellEffectTarget(64));
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        FightCastScope scope = makeCastScope(caster, spell, effect, target.cell());

        assertFalse(hook.apply(handler, scope, scope.effects().get(0)));

        Buff buff = target.buffs().stream().filter(b -> b.effect().effect() == 96).findFirst().get();
        requestStack.assertLast(new AddBuff(buff));
        requestStack.clear();

        target.buffs().onCharacteristicAltered(Characteristic.ACTION_POINT, -3);
        assertEquals(30, target.characteristics().get(Characteristic.FIXED_DAMAGE));

        List<Buff> buffs = target.buffs().stream().filter(b -> b.effect().effect() == 96 && b.hook().equals(handler)).collect(Collectors.toList());
        assertCount(3, buffs);
        assertEquals(10, buffs.get(0).effect().min());
        assertEquals(10, buffs.get(1).effect().min());
        assertEquals(10, buffs.get(2).effect().min());

        requestStack.assertAll(
            ActionEffect.buff(buffs.get(0), 10),
            new AddBuff(buffs.get(0)),
            ActionEffect.buff(buffs.get(1), 10),
            new AddBuff(buffs.get(1)),
            ActionEffect.buff(buffs.get(2), 10),
            new AddBuff(buffs.get(2))
        );
    }

    @Test
    void applyWithoutEffectShouldIgnore() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.effect()).thenReturn(96);
        Mockito.when(effect.min()).thenReturn(10);
        Mockito.when(effect.duration()).thenReturn(5);
        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(new SpellEffectTarget(64));
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        FightCastScope scope = makeCastScope(caster, spell, effect, target.cell());

        assertFalse(hook.apply(handler, scope, scope.effects().get(0)));

        Buff buff = target.buffs().stream().filter(b -> b.effect().effect() == 96).findFirst().get();
        requestStack.assertLast(new AddBuff(buff));
        requestStack.clear();

        target.buffs().onCharacteristicAltered(Characteristic.ACTION_POINT, 0);
        assertEquals(0, target.characteristics().get(Characteristic.FIXED_DAMAGE));

        assertFalse(target.buffs().stream().anyMatch(b -> b.effect().effect() == 96 && b.hook().equals(handler)));

        requestStack.assertEmpty();
    }

    @Test
    void applyShouldIgnoredAddingAp() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.effect()).thenReturn(96);
        Mockito.when(effect.min()).thenReturn(10);
        Mockito.when(effect.duration()).thenReturn(5);
        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(new SpellEffectTarget(64));
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        FightCastScope scope = makeCastScope(caster, spell, effect, target.cell());

        assertFalse(hook.apply(handler, scope, scope.effects().get(0)));

        Buff buff = target.buffs().stream().filter(b -> b.effect().effect() == 96).findFirst().get();
        requestStack.assertLast(new AddBuff(buff));
        requestStack.clear();

        target.buffs().onCharacteristicAltered(Characteristic.ACTION_POINT, 10);
        assertEquals(0, target.characteristics().get(Characteristic.FIXED_DAMAGE));

        assertFalse(target.buffs().stream().anyMatch(b -> b.effect().effect() == 96 && b.hook().equals(handler)));

        requestStack.assertEmpty();
    }

    @Test
    void applyShouldIgnoredNotActionPointAlteration() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.effect()).thenReturn(96);
        Mockito.when(effect.min()).thenReturn(10);
        Mockito.when(effect.duration()).thenReturn(5);
        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(new SpellEffectTarget(64));
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        FightCastScope scope = makeCastScope(caster, spell, effect, target.cell());

        assertFalse(hook.apply(handler, scope, scope.effects().get(0)));

        Buff buff = target.buffs().stream().filter(b -> b.effect().effect() == 96).findFirst().get();
        requestStack.assertLast(new AddBuff(buff));
        requestStack.clear();

        target.buffs().onCharacteristicAltered(Characteristic.MOVEMENT_POINT, -10);
        assertEquals(0, target.characteristics().get(Characteristic.FIXED_DAMAGE));

        assertFalse(target.buffs().stream().anyMatch(b -> b.effect().effect() == 96 && b.hook().equals(handler)));

        requestStack.assertEmpty();
    }

    @Test
    void applyArea() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.effect()).thenReturn(96);
        Mockito.when(effect.min()).thenReturn(10);
        Mockito.when(effect.area()).thenReturn(new CircleArea(new EffectArea(EffectArea.Type.CIRCLE, 10)));
        Mockito.when(effect.target()).thenReturn(new SpellEffectTarget(64));
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        FightCastScope scope = makeCastScope(caster, spell, effect, target.cell());
        requestStack.clear();

        assertFalse(hook.apply(handler, scope, scope.effects().get(0)));

        Buff buff1 = target.buffs().stream().filter(b -> b.effect().effect() == 96).findFirst().get();
        Buff buff2 = caster.buffs().stream().filter(b -> b.effect().effect() == 96).findFirst().get();

        requestStack.assertAll(
            new AddBuff(buff1),
            new AddBuff(buff2)
        );
        requestStack.clear();

        target.buffs().onCharacteristicAltered(Characteristic.ACTION_POINT, -3);
        assertEquals(30, target.characteristics().get(Characteristic.FIXED_DAMAGE));
    }
}
