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
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buff;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import fr.quatrevieux.araknemu.network.game.out.fight.action.ActionEffect;
import fr.quatrevieux.araknemu.network.game.out.fight.turn.TurnMiddle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AlterVitalityHookTest extends FightBaseCase {
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
        AlterVitalityHook hook = AlterVitalityHook.add(fight);
        requestStack.clear();

        SpellEffect effect = Mockito.mock(SpellEffect.class);

        Mockito.when(effect.effect()).thenReturn(123);
        Mockito.when(effect.min()).thenReturn(50);
        Mockito.when(effect.duration()).thenReturn(5);

        Buff buff = new Buff(effect, Mockito.mock(Spell.class), caster, target, hook);
        hook.onBuffStarted(buff);

        requestStack.assertAll(
            new TurnMiddle(fight.fighters()),
            ActionEffect.buff(buff, 50)
        );
        assertEquals(50, target.characteristics().get(Characteristic.VITALITY));
        assertEquals(100, target.life().max());
        assertEquals(100, target.life().current());

        hook.onBuffTerminated(buff);
        requestStack.assertLast(new TurnMiddle(fight.fighters()));
        assertEquals(0, target.characteristics().get(Characteristic.VITALITY));
        assertEquals(50, target.life().max());
        assertEquals(50, target.life().current());
    }

    @Test
    void remove() {
        AlterVitalityHook hook = AlterVitalityHook.remove(fight);
        requestStack.clear();

        SpellEffect effect = Mockito.mock(SpellEffect.class);

        Mockito.when(effect.effect()).thenReturn(123);
        Mockito.when(effect.min()).thenReturn(20);
        Mockito.when(effect.duration()).thenReturn(5);

        Buff buff = new Buff(effect, Mockito.mock(Spell.class), caster, target, hook);
        hook.onBuffStarted(buff);

        requestStack.assertAll(
            new TurnMiddle(fight.fighters()),
            ActionEffect.buff(buff, 20)
        );
        assertEquals(-20, target.characteristics().get(Characteristic.VITALITY));
        assertEquals(30, target.life().max());
        assertEquals(30, target.life().current());

        hook.onBuffTerminated(buff);
        requestStack.assertLast(new TurnMiddle(fight.fighters()));
        assertEquals(0, target.characteristics().get(Characteristic.VITALITY));
        assertEquals(50, target.life().max());
        assertEquals(50, target.life().current());
    }
}
