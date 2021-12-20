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

package fr.quatrevieux.araknemu.game.fight.castable.effect.handler.characteristic;

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

class AlterCharacteristicHookTest extends FightBaseCase {
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
    void add() {
        AlterCharacteristicHook hook = AlterCharacteristicHook.add(fight, Characteristic.STRENGTH);

        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);

        Mockito.when(effect.effect()).thenReturn(111);
        Mockito.when(effect.min()).thenReturn(1);
        Mockito.when(effect.min()).thenReturn(5);
        Mockito.when(effect.duration()).thenReturn(1);

        Buff buff = new Buff(new BuffEffect(effect, 50), spell, caster, target, hook);
        target.buffs().add(buff);

        assertEquals(50, target.characteristics().get(Characteristic.STRENGTH));
        requestStack.assertOne(ActionEffect.buff(buff, 50));

        target.buffs().refresh();
        assertEquals(0, target.characteristics().get(Characteristic.STRENGTH));
    }

    @Test
    void remove() {
        AlterCharacteristicHook hook = AlterCharacteristicHook.remove(fight, Characteristic.STRENGTH);

        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);

        Mockito.when(effect.effect()).thenReturn(111);
        Mockito.when(effect.min()).thenReturn(1);
        Mockito.when(effect.min()).thenReturn(5);
        Mockito.when(effect.duration()).thenReturn(1);

        Buff buff = new Buff(new BuffEffect(effect, 50), spell, caster, target, hook);
        target.buffs().add(buff);

        assertEquals(-50, target.characteristics().get(Characteristic.STRENGTH));
        requestStack.assertOne(ActionEffect.buff(buff, -50));

        target.buffs().refresh();
        assertEquals(0, target.characteristics().get(Characteristic.STRENGTH));
    }
}
