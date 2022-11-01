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
import fr.quatrevieux.araknemu.game.fight.castable.FightCastScope;
import fr.quatrevieux.araknemu.game.fight.castable.effect.Element;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buff;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.damage.DamageApplier;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
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
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AddCharacteristicOnDamageHandlerTest extends FightBaseCase {
    private Fight fight;
    private PlayerFighter caster;
    private PlayerFighter target;
    private AddCharacteristicOnDamageHandler handler;

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

        handler = new AddCharacteristicOnDamageHandler(fight)
            .register(108, AlterVitalityHook.add(fight))
            .register(118, Characteristic.STRENGTH)
            .register(119, Characteristic.AGILITY)
            .register(123, Characteristic.LUCK)
            .register(129, Characteristic.INTELLIGENCE)
            .register(138, Characteristic.PERCENT_DAMAGE)
        ;

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
    void buff() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.effect()).thenReturn(788);
        Mockito.when(effect.min()).thenReturn(123);
        Mockito.when(effect.min()).thenReturn(10);
        Mockito.when(effect.max()).thenReturn(3);
        Mockito.when(effect.duration()).thenReturn(5);
        Mockito.when(effect.area()).thenReturn(new CircleArea(new EffectArea(EffectArea.Type.CIRCLE, 10)));
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        FightCastScope scope = makeCastScope(caster, spell, effect, caster.cell());
        handler.buff(scope, scope.effects().get(0));

        Optional<Buff> buff1 = caster.buffs().stream().filter(buff -> buff.effect().effect() == 788).findFirst();
        Optional<Buff> buff2 = target.buffs().stream().filter(buff -> buff.effect().effect() == 788).findFirst();

        assertTrue(buff1.isPresent());
        assertTrue(buff2.isPresent());

        assertEquals(effect, buff1.get().effect());
        assertEquals(effect, buff2.get().effect());
        assertFalse(buff1.get().canBeDispelled());
        assertFalse(buff2.get().canBeDispelled());
    }

    @Test
    void functionalOnDamage() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.effect()).thenReturn(788);
        Mockito.when(effect.min()).thenReturn(123);
        Mockito.when(effect.max()).thenReturn(50);
        Mockito.when(effect.special()).thenReturn(3);
        Mockito.when(effect.duration()).thenReturn(5);
        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        FightCastScope scope = makeCastScope(caster, spell, effect, target.cell());
        handler.buff(scope, scope.effects().get(0));

        requestStack.clear();

        new DamageApplier(Element.FIRE, fight).applyFixed(caster, 10, target);

        Buff characBuff = target.buffs().stream().filter(buff -> buff.effect().effect() == 123).findFirst().get();

        assertEquals(10, characBuff.effect().min());
        assertEquals(3, characBuff.effect().duration());
        assertTrue(characBuff.canBeDispelled());
        assertEquals(10, target.characteristics().get(Characteristic.LUCK));

        requestStack.assertOne(ActionEffect.buff(characBuff, 10));
        requestStack.clear();

        new DamageApplier(Element.FIRE, fight).applyFixed(caster, 15, target);
        characBuff = target.buffs().stream().filter(buff -> buff.effect().effect() == 123).collect(Collectors.toList()).get(1);

        assertEquals(15, characBuff.effect().min());
        assertEquals(3, characBuff.effect().duration());
        assertTrue(characBuff.canBeDispelled());
        assertEquals(25, target.characteristics().get(Characteristic.LUCK));
    }

    @Test
    void unsupportedEffect() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.effect()).thenReturn(788);
        Mockito.when(effect.min()).thenReturn(404);
        Mockito.when(effect.max()).thenReturn(50);
        Mockito.when(effect.special()).thenReturn(3);
        Mockito.when(effect.duration()).thenReturn(5);
        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        FightCastScope scope = makeCastScope(caster, spell, effect, target.cell());
        handler.buff(scope, scope.effects().get(0));

        requestStack.clear();

        assertThrows(IllegalArgumentException.class, () -> new DamageApplier(Element.FIRE, fight).applyFixed(caster, 10, target));
    }

    @Test
    void functionalCharacteristicBoostDependOnFirstSpellEffectParameter() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.effect()).thenReturn(788);
        Mockito.when(effect.min()).thenReturn(138);
        Mockito.when(effect.max()).thenReturn(50);
        Mockito.when(effect.special()).thenReturn(3);
        Mockito.when(effect.duration()).thenReturn(5);
        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        FightCastScope scope = makeCastScope(caster, spell, effect, target.cell());
        handler.buff(scope, scope.effects().get(0));

        requestStack.clear();

        new DamageApplier(Element.FIRE, fight).applyFixed(caster, 10, target);

        Buff characBuff = target.buffs().stream().filter(buff -> buff.effect().effect() == 138).findFirst().get();

        assertEquals(10, characBuff.effect().min());
        assertEquals(3, characBuff.effect().duration());
        assertTrue(characBuff.canBeDispelled());
        assertEquals(10, target.characteristics().get(Characteristic.PERCENT_DAMAGE));

        requestStack.assertOne(ActionEffect.buff(characBuff, 10));
    }

    @Test
    void functionalDamageHigherThanMaximumBuff() {
        target.life().alterMax(target, 1000);

        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.effect()).thenReturn(788);
        Mockito.when(effect.min()).thenReturn(123);
        Mockito.when(effect.max()).thenReturn(50);
        Mockito.when(effect.special()).thenReturn(3);
        Mockito.when(effect.duration()).thenReturn(5);
        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        FightCastScope scope = makeCastScope(caster, spell, effect, target.cell());
        handler.buff(scope, scope.effects().get(0));

        requestStack.clear();

        new DamageApplier(Element.FIRE, fight).applyFixed(caster, 100, target);

        Buff characBuff = target.buffs().stream().filter(buff -> buff.effect().effect() == 123).findFirst().get();

        assertEquals(50, characBuff.effect().min());
        assertEquals(3, characBuff.effect().duration());
        assertTrue(characBuff.canBeDispelled());
        assertEquals(50, target.characteristics().get(Characteristic.LUCK));

        requestStack.assertOne(ActionEffect.buff(characBuff, 50));
        requestStack.clear();

        new DamageApplier(Element.FIRE, fight).applyFixed(caster, 15, target);
        assertEquals(1, target.buffs().stream().filter(buff -> buff.effect().effect() == 123).count());
        assertEquals(50, target.characteristics().get(Characteristic.LUCK));
    }

    @Test
    void functionalShouldBeLimitedByMaximum() {
        target.life().alterMax(target, 1000);

        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.effect()).thenReturn(788);
        Mockito.when(effect.min()).thenReturn(123);
        Mockito.when(effect.max()).thenReturn(50);
        Mockito.when(effect.special()).thenReturn(3);
        Mockito.when(effect.duration()).thenReturn(5);
        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        FightCastScope scope = makeCastScope(caster, spell, effect, target.cell());
        handler.buff(scope, scope.effects().get(0));

        requestStack.clear();

        new DamageApplier(Element.FIRE, fight).applyFixed(caster, 30, target);

        Buff characBuff = target.buffs().stream().filter(buff -> buff.effect().effect() == 123).findFirst().get();

        assertEquals(30, characBuff.effect().min());
        assertEquals(3, characBuff.effect().duration());
        assertTrue(characBuff.canBeDispelled());
        assertEquals(30, target.characteristics().get(Characteristic.LUCK));

        requestStack.assertOne(ActionEffect.buff(characBuff, 30));
        requestStack.clear();

        new DamageApplier(Element.FIRE, fight).applyFixed(caster, 30, target);
        assertEquals(2, target.buffs().stream().filter(buff -> buff.effect().effect() == 123).count());
        assertEquals(50, target.characteristics().get(Characteristic.LUCK));
    }

    @Test
    void functionalLimitShouldBeResetBuffRefresh() {
        target.life().alterMax(target, 1000);

        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.effect()).thenReturn(788);
        Mockito.when(effect.min()).thenReturn(123);
        Mockito.when(effect.max()).thenReturn(50);
        Mockito.when(effect.special()).thenReturn(3);
        Mockito.when(effect.duration()).thenReturn(5);
        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        FightCastScope scope = makeCastScope(caster, spell, effect, target.cell());
        handler.buff(scope, scope.effects().get(0));

        new DamageApplier(Element.FIRE, fight).applyFixed(caster, 100, target);
        assertEquals(50, target.characteristics().get(Characteristic.LUCK));

        target.buffs().refresh();
        new DamageApplier(Element.FIRE, fight).applyFixed(caster, 100, target);
        assertEquals(100, target.characteristics().get(Characteristic.LUCK));

        target.buffs().refresh();
        new DamageApplier(Element.FIRE, fight).applyFixed(caster, 100, target);
        assertEquals(150, target.characteristics().get(Characteristic.LUCK));

        target.buffs().refresh();
        new DamageApplier(Element.FIRE, fight).applyFixed(caster, 100, target);
        assertEquals(150, target.characteristics().get(Characteristic.LUCK)); // first buff is terminated
    }

    @Test
    void functionalOnSelfDamage() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.effect()).thenReturn(788);
        Mockito.when(effect.min()).thenReturn(123);
        Mockito.when(effect.max()).thenReturn(50);
        Mockito.when(effect.special()).thenReturn(3);
        Mockito.when(effect.duration()).thenReturn(5);
        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        FightCastScope scope = makeCastScope(caster, spell, effect, caster.cell());
        handler.buff(scope, scope.effects().get(0));

        requestStack.clear();

        new DamageApplier(Element.FIRE, fight).applyFixed(caster, 30, caster);

        Buff characBuff = caster.buffs().stream().filter(buff -> buff.effect().effect() == 123).findFirst().get();

        assertEquals(30, characBuff.effect().min());
        assertEquals(4, characBuff.remainingTurns());
        assertTrue(characBuff.canBeDispelled());
        assertEquals(30, caster.characteristics().get(Characteristic.LUCK));

        requestStack.assertOne(ActionEffect.buff(characBuff, 30));
        requestStack.clear();

        new DamageApplier(Element.FIRE, fight).applyFixed(caster, 30, caster);
        assertEquals(50, caster.characteristics().get(Characteristic.LUCK));
    }
}
