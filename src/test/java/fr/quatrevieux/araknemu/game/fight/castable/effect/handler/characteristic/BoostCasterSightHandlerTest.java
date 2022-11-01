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
import fr.quatrevieux.araknemu.game.fight.castable.effect.EffectValue;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buff;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.BuffHook;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BoostCasterSightHandlerTest extends FightBaseCase {
    private Fight fight;
    private PlayerFighter caster;
    private PlayerFighter target;
    private BoostCasterSightHandler handler;

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

        handler = new BoostCasterSightHandler(fight);

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
    void buffShouldOnlyTargetCaster() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.effect()).thenReturn(123);
        Mockito.when(effect.min()).thenReturn(50);
        Mockito.when(effect.max()).thenReturn(60);
        Mockito.when(effect.duration()).thenReturn(5);
        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        FightCastScope scope = makeCastScope(caster, spell, effect, target.cell());
        handler.buff(scope, scope.effects().get(0));

        Optional<Buff> buff1 = caster.buffs().stream().filter(buff -> buff.effect().effect() == 123).findFirst();
        Optional<Buff> buff2 = target.buffs().stream().filter(buff -> buff.effect().effect() == 123).findFirst();

        assertTrue(buff1.isPresent());
        assertFalse(buff2.isPresent());

        assertBetween(50, 60, buff1.get().effect().min());
        assertEquals(buff1.get().effect().min(), caster.characteristics().get(Characteristic.SIGHT_BOOST));
    }

    @Test
    void buffMaximized() {
        caster.buffs().add(new Buff(Mockito.mock(SpellEffect.class), Mockito.mock(Spell.class), caster, caster, new BuffHook() {
            @Override
            public void onEffectValueTarget(Buff buff, EffectValue value) {
                value.maximize();
            }
        }));

        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.effect()).thenReturn(123);
        Mockito.when(effect.min()).thenReturn(0);
        Mockito.when(effect.max()).thenReturn(10000);
        Mockito.when(effect.duration()).thenReturn(5);
        Mockito.when(effect.area()).thenReturn(new CircleArea(new EffectArea(EffectArea.Type.CIRCLE, 10)));
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        FightCastScope scope = makeCastScope(caster, spell, effect, target.cell());
        handler.buff(scope, scope.effects().get(0));

        Optional<Buff> buff1 = caster.buffs().stream().filter(buff -> buff.effect().effect() == 123).findFirst();
        Optional<Buff> buff2 = target.buffs().stream().filter(buff -> buff.effect().effect() == 123).findFirst();

        assertTrue(buff1.isPresent());
        assertFalse(buff2.isPresent());

        assertEquals(10000, buff1.get().effect().min());
        assertEquals(10000, caster.characteristics().get(Characteristic.SIGHT_BOOST));
    }

    @Test
    void onBuffStartedAndTerminated() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.effect()).thenReturn(123);
        Mockito.when(effect.min()).thenReturn(5);
        Mockito.when(effect.duration()).thenReturn(5);
        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        FightCastScope scope = makeCastScope(caster, spell, effect, target.cell());
        handler.buff(scope, scope.effects().get(0));

        requestStack.assertOne(ActionEffect.boostSight(caster, caster, 5, 5));
        assertEquals(5, caster.characteristics().get(Characteristic.SIGHT_BOOST));

        caster.buffs().refresh();
        caster.buffs().refresh();
        caster.buffs().refresh();
        caster.buffs().refresh();
        caster.buffs().refresh();
        caster.buffs().refresh();

        assertEquals(0, caster.characteristics().get(Characteristic.SIGHT_BOOST));
    }
}
