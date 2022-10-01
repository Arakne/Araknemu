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
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.castable.CastScope;
import fr.quatrevieux.araknemu.game.fight.castable.FightCastScope;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buff;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterData;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.spell.SpellConstraints;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import fr.quatrevieux.araknemu.game.spell.effect.area.CellArea;
import fr.quatrevieux.araknemu.game.spell.effect.target.SpellEffectTarget;
import fr.quatrevieux.araknemu.network.game.out.account.Stats;
import fr.quatrevieux.araknemu.network.game.out.fight.AddBuff;
import fr.quatrevieux.araknemu.network.game.out.fight.action.ActionEffect;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class MovementPointLostApplierTest extends FightBaseCase {
    private Fight fight;
    private PlayerFighter caster;
    private PlayerFighter target;
    private MovementPointLostApplier applier;

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

        applier = new MovementPointLostApplier(fight);

        requestStack.clear();
    }

    @Test
    void computeLostPointsWithoutPoints() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = AbstractPointLostApplier.class.getDeclaredMethod("computePointLost", FighterData.class, FighterData.class, int.class);
        method.setAccessible(true);

        target.characteristics().alter(Characteristic.MOVEMENT_POINT, -3);

        assertEquals(0, method.invoke(applier, caster, target, 100));
    }

    @Test
    void computeLostPointsWithoutMaxPointsButBoosted() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = AbstractPointLostApplier.class.getDeclaredMethod("computePointLost", FighterData.class, FighterData.class, int.class);
        method.setAccessible(true);

        target.player().properties().characteristics().base().set(Characteristic.MOVEMENT_POINT, -3);
        target.characteristics().alter(Characteristic.MOVEMENT_POINT, 3);

        assertEquals(3, method.invoke(applier, caster, target, 100));
        assertEquals(2, method.invoke(applier, caster, target, 2));
    }

    @ParameterizedTest
    @MethodSource("provideLostPoints")
    void computeLostPoints(int casterWisdom, int targetResistance, int baseValue, double expectedAvg) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = AbstractPointLostApplier.class.getDeclaredMethod("computePointLost", FighterData.class, FighterData.class, int.class);
        method.setAccessible(true);

        target.characteristics().alter(Characteristic.RESISTANCE_MOVEMENT_POINT, targetResistance);
        caster.characteristics().alter(Characteristic.WISDOM, casterWisdom);

        double total = 0;

        for (int i = 0; i < 100; ++i) {
            total += (int) method.invoke(applier, caster, target, baseValue);
        }

        assertEquals(expectedAvg, total / 100, 0.1);
    }

    public static Stream<Arguments> provideLostPoints() {
        return Stream.of(
            Arguments.of(100, 0, 1, .9),
            Arguments.of(100, 0, 2, 1.8),
            Arguments.of(100, 0, 3, 2.7),

            Arguments.of(0, 25, 1, .1),
            Arguments.of(0, 25, 2, .2),
            Arguments.of(0, 25, 3, .3),

            Arguments.of(100, 25, 1, .5),
            Arguments.of(100, 25, 2, .9),
            Arguments.of(100, 25, 3, 1.3),
            Arguments.of(100, 25, 4, 1.5),
            Arguments.of(100, 25, 5, 1.75),
            Arguments.of(100, 25, 6, 2),
            Arguments.of(100, 25, 7, 2),
            Arguments.of(100, 25, 8, 2.3),
            Arguments.of(100, 25, 9, 2.4),
            Arguments.of(100, 25, 10, 2.5),

            Arguments.of(400, 0, 1, .9),
            Arguments.of(400, 10, 1, .9),
            Arguments.of(400, 50, 1, .9),
            Arguments.of(400, 75, 1, .67),
            Arguments.of(400, 90, 1, .55),
            Arguments.of(400, 100, 1, .5),
            Arguments.of(400, 150, 1, .33),
            Arguments.of(400, 200, 1, .25),
            Arguments.of(400, 300, 1, .17),

            Arguments.of(0, 0, 100, 3)
        );
    }

    @Test
    void applyNotDodged() {
        caster.characteristics().alter(Characteristic.WISDOM, 100);
        requestStack.clear();

        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.effect()).thenReturn(127);
        Mockito.when(effect.min()).thenReturn(2);
        Mockito.when(effect.duration()).thenReturn(5);
        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        FightCastScope scope = makeCastScope(caster, spell, effect, target.cell());
        applier.apply(scope, target, effect);

        Buff buff = target.buffs().stream().filter(b -> b.effect().effect() == 127).findFirst().get();

        assertEquals(1, target.characteristics().get(Characteristic.MOVEMENT_POINT));
        assertEquals(2, buff.effect().min());

        requestStack.assertAll(
            ActionEffect.buff(buff, -2),
            new AddBuff(buff)
        );
    }

    @Test
    void applyOnActiveTurnShouldRemoveAPFromTurn() {
        caster.characteristics().alter(Characteristic.WISDOM, 1000);
        caster.characteristics().alter(Characteristic.RESISTANCE_MOVEMENT_POINT, -1000);
        requestStack.clear();

        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.effect()).thenReturn(127);
        Mockito.when(effect.min()).thenReturn(2);
        Mockito.when(effect.duration()).thenReturn(5);
        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        FightCastScope scope = makeCastScope(caster, spell, effect, caster.cell());
        applier.apply(scope, caster, effect);

        Buff buff = caster.buffs().stream().filter(b -> b.effect().effect() == 127).findFirst().get();

        assertEquals(1, caster.characteristics().get(Characteristic.MOVEMENT_POINT));
        assertEquals(1, caster.turn().points().movementPoints());
        assertEquals(2, buff.effect().min());

        requestStack.assertAll(
            new Stats(caster.properties()),
            ActionEffect.buff(buff, -2),
            "GIE127;1;2;;0;;5;0"
        );
    }

    @Test
    void applyTotallyDodged() {
        target.characteristics().alter(Characteristic.RESISTANCE_MOVEMENT_POINT, 100);
        requestStack.clear();

        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.effect()).thenReturn(127);
        Mockito.when(effect.min()).thenReturn(2);
        Mockito.when(effect.duration()).thenReturn(5);
        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        FightCastScope scope = makeCastScope(caster, spell, effect, target.cell());
        applier.apply(scope, target, effect);

        assertFalse(target.buffs().stream().anyMatch(b -> b.effect().effect() == 127));
        assertEquals(3, target.characteristics().get(Characteristic.MOVEMENT_POINT));

        requestStack.assertAll(new ActionEffect(309, caster, target.id(), 2));
    }

    @Test
    void applyPartiallyDodged() {
        requestStack.clear();

        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.effect()).thenReturn(127);
        Mockito.when(effect.min()).thenReturn(2);
        Mockito.when(effect.duration()).thenReturn(5);
        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        FightCastScope scope = makeCastScope(caster, spell, effect, target.cell());
        applier.apply(scope, target, effect);

        Buff buff = target.buffs().stream().filter(b -> b.effect().effect() == 127).findFirst().get();

        assertEquals(2, target.characteristics().get(Characteristic.MOVEMENT_POINT));
        assertEquals(1, buff.effect().min());

        requestStack.assertAll(
            new ActionEffect(309, caster, target.id(), 1),
            ActionEffect.buff(buff, -1),
            new AddBuff(buff)
        );
    }
}
