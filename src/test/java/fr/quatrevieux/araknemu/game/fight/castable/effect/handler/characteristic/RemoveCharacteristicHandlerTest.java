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

package fr.quatrevieux.araknemu.game.fight.castable.effect.handler.characteristic;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.data.value.EffectArea;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.castable.FightCastScope;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.FightBuff;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.BuffHook;
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
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RemoveCharacteristicHandlerTest extends FightBaseCase {
    private Fight fight;
    private PlayerFighter caster;
    private PlayerFighter target;
    private RemoveCharacteristicHandler handler;

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

        handler = new RemoveCharacteristicHandler(fight, Characteristic.LUCK);

        requestStack.clear();
    }

    @Test
    void handle() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.effect()).thenReturn(123);
        Mockito.when(effect.min()).thenReturn(50);
        Mockito.when(effect.max()).thenReturn(60);
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        FightCastScope scope = makeCastScope(caster, spell, effect, caster.cell());
        handler.handle(scope, scope.effects().get(0));

        Optional<FightBuff> buff1 = caster.buffs().stream().filter(buff -> buff.effect().effect() == 123).findFirst();

        assertTrue(buff1.isPresent());
        assertBetween(50, 60, buff1.get().effect().min());
        assertEquals(1, buff1.get().remainingTurns());
        assertTrue(buff1.get().canBeDispelled());
    }

    @Test
    void buff() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.effect()).thenReturn(152);
        Mockito.when(effect.min()).thenReturn(50);
        Mockito.when(effect.min()).thenReturn(60);
        Mockito.when(effect.duration()).thenReturn(5);
        Mockito.when(effect.area()).thenReturn(new CircleArea(new EffectArea(EffectArea.Type.CIRCLE, 10)));
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        FightCastScope scope = makeCastScope(caster, spell, effect, caster.cell());
        handler.buff(scope, scope.effects().get(0));

        Optional<FightBuff> buff1 = caster.buffs().stream().filter(buff -> buff.effect().effect() == 152).findFirst();
        Optional<FightBuff> buff2 = target.buffs().stream().filter(buff -> buff.effect().effect() == 152).findFirst();

        assertTrue(buff1.isPresent());
        assertTrue(buff2.isPresent());

        assertBetween(50, 60, buff1.get().effect().min());
        assertEquals(buff1.get().effect().min(), buff2.get().effect().min());
    }

    @Test
    void buffShouldCallOnCharacteristicAltered() {
        AtomicReference<Characteristic> hookCharacteristic = new AtomicReference<>();
        AtomicReference<Integer> hookValue = new AtomicReference<>();

        caster.buffs().add(new FightBuff(Mockito.mock(SpellEffect.class), Mockito.mock(Spell.class), caster, caster, new BuffHook() {
            @Override
            public void onCharacteristicAltered(FightBuff buff, Characteristic characteristic, int value) {
                hookCharacteristic.set(characteristic);
                hookValue.set(value);
            }
        }, true));

        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.effect()).thenReturn(111);
        Mockito.when(effect.min()).thenReturn(50);
        Mockito.when(effect.duration()).thenReturn(5);
        Mockito.when(effect.area()).thenReturn(new CircleArea(new EffectArea(EffectArea.Type.CIRCLE, 10)));
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        FightCastScope scope = makeCastScope(caster, spell, effect, caster.cell());
        handler.buff(scope, scope.effects().get(0));

        assertEquals(Characteristic.LUCK, hookCharacteristic.get());
        assertEquals(-50, hookValue.get());
    }

    @Test
    void onBuffStartedAndTerminated() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);

        Mockito.when(effect.effect()).thenReturn(152);
        Mockito.when(effect.min()).thenReturn(50);
        Mockito.when(effect.duration()).thenReturn(5);

        FightBuff buff = new FightBuff(effect, Mockito.mock(Spell.class), caster, target, handler);

        handler.onBuffStarted(buff);

        requestStack.assertLast(ActionEffect.buff(buff, 50));
        assertEquals(-50, target.characteristics().get(Characteristic.LUCK));

        handler.onBuffTerminated(buff);
        assertEquals(0, target.characteristics().get(Characteristic.LUCK));
    }

    @Test
    void applyFromHook() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.effect()).thenReturn(123);
        Mockito.when(effect.min()).thenReturn(50);
        Mockito.when(effect.max()).thenReturn(60);
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(effect.duration()).thenReturn(5);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        handler.applyFromHook(new FightBuff(effect, spell, caster, target, handler));

        FightBuff buff = target.buffs().stream().filter(b -> b.effect().effect() == 123).findFirst().get();

        assertBetween(50, 60, buff.effect().min());
        assertEquals(0,buff.effect().max());
        assertEquals(5, buff.remainingTurns());
        assertTrue(buff.canBeDispelled());
        assertBetween(-60, -50, target.characteristics().get(Characteristic.LUCK));
    }
}
