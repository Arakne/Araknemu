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
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buff;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.BuffEffect;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import fr.quatrevieux.araknemu.network.game.out.fight.action.ActionEffect;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AlterPointHookTest extends FightBaseCase {
    private Fight fight;
    private PlayerFighter caster;
    private PlayerFighter target;

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
        requestStack.clear();
    }

    @Test
    void addActionPoint() {
        AlterPointHook hook = AlterPointHook.addActionPoint(fight);

        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);

        Mockito.when(effect.effect()).thenReturn(111);
        Mockito.when(effect.min()).thenReturn(1);
        Mockito.when(effect.min()).thenReturn(5);
        Mockito.when(effect.duration()).thenReturn(1);

        Buff buff = new Buff(BuffEffect.fixed(effect, 2), spell, caster, caster, hook);
        caster.buffs().add(buff);

        assertEquals(8, caster.characteristics().get(Characteristic.ACTION_POINT));
        assertEquals(8, caster.turn().points().actionPoints());
        requestStack.assertOne(ActionEffect.buff(buff, 2));

        caster.buffs().refresh();
        caster.buffs().refresh();
        assertEquals(6, caster.characteristics().get(Characteristic.ACTION_POINT));
    }

    @Test
    void removeActionPoint() {
        AlterPointHook hook = AlterPointHook.removeActionPoint(fight);

        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);

        Mockito.when(effect.effect()).thenReturn(111);
        Mockito.when(effect.min()).thenReturn(1);
        Mockito.when(effect.min()).thenReturn(5);
        Mockito.when(effect.duration()).thenReturn(1);

        Buff buff = new Buff(BuffEffect.fixed(effect, 2), spell, caster, caster, hook);
        caster.buffs().add(buff);

        assertEquals(4, caster.characteristics().get(Characteristic.ACTION_POINT));
        assertEquals(4, caster.turn().points().actionPoints());
        requestStack.assertOne(ActionEffect.buff(buff, -2));

        caster.buffs().refresh();
        caster.buffs().refresh();
        assertEquals(6, caster.characteristics().get(Characteristic.ACTION_POINT));
    }

    @Test
    void addMovementPoint() {
        AlterPointHook hook = AlterPointHook.addMovementPoint(fight);

        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);

        Mockito.when(effect.effect()).thenReturn(111);
        Mockito.when(effect.min()).thenReturn(1);
        Mockito.when(effect.min()).thenReturn(5);
        Mockito.when(effect.duration()).thenReturn(1);

        Buff buff = new Buff(BuffEffect.fixed(effect, 2), spell, caster, caster, hook);
        caster.buffs().add(buff);

        assertEquals(5, caster.characteristics().get(Characteristic.MOVEMENT_POINT));
        assertEquals(5, caster.turn().points().movementPoints());
        requestStack.assertOne(ActionEffect.buff(buff, 2));

        caster.buffs().refresh();
        caster.buffs().refresh();
        assertEquals(3, caster.characteristics().get(Characteristic.MOVEMENT_POINT));
    }

    @Test
    void removeMovementPoint() {
        AlterPointHook hook = AlterPointHook.removeMovementPoint(fight);

        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);

        Mockito.when(effect.effect()).thenReturn(111);
        Mockito.when(effect.min()).thenReturn(1);
        Mockito.when(effect.min()).thenReturn(5);
        Mockito.when(effect.duration()).thenReturn(1);

        Buff buff = new Buff(BuffEffect.fixed(effect, 2), spell, caster, caster, hook);
        caster.buffs().add(buff);

        assertEquals(1, caster.characteristics().get(Characteristic.MOVEMENT_POINT));
        assertEquals(1, caster.turn().points().movementPoints());
        requestStack.assertOne(ActionEffect.buff(buff, -2));

        caster.buffs().refresh();
        caster.buffs().refresh();
        assertEquals(3, caster.characteristics().get(Characteristic.MOVEMENT_POINT));
    }
}
