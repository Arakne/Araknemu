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

package fr.quatrevieux.araknemu.game.listener.fight.fighter;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.FightBuff;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.BuffHook;
import fr.quatrevieux.araknemu.game.fight.fighter.event.FighterDie;
import fr.quatrevieux.araknemu.game.listener.fight.CheckFightTerminated;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import fr.quatrevieux.araknemu.network.game.out.fight.AddBuff;
import fr.quatrevieux.araknemu.network.game.out.fight.ClearAllBuffs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;

class DispelDeadFighterBuffTest extends FightBaseCase {
    private Fight fight;
    private DispelDeadFighterBuff listener;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        fight = createFight();
        fight.nextState();
        fight.turnList().start();
        listener = new DispelDeadFighterBuff(fight);

        fight.dispatcher().remove(CheckFightTerminated.class);
        fight.dispatcher().remove(DispelDeadFighterBuff.class);

        requestStack.clear();
    }

    @Test
    void onFighterDie() {
        FightBuff buff1 = new FightBuff(Mockito.mock(SpellEffect.class), Mockito.mock(Spell.class), other.fighter(), player.fighter(), Mockito.mock(BuffHook.class));
        FightBuff buff2 = new FightBuff(Mockito.mock(SpellEffect.class), Mockito.mock(Spell.class), player.fighter(), player.fighter(), Mockito.mock(BuffHook.class), false);
        FightBuff buff3 = new FightBuff(Mockito.mock(SpellEffect.class), Mockito.mock(Spell.class), other.fighter(), player.fighter(), Mockito.mock(BuffHook.class), true);

        player.fighter().buffs().add(buff1);
        player.fighter().buffs().add(buff2);
        player.fighter().buffs().add(buff3);

        requestStack.clear();

        listener.on(new FighterDie(other.fighter(), other.fighter()));

        assertIterableEquals(Collections.singletonList(buff2), player.fighter().buffs());

        requestStack.assertAll(
            new ClearAllBuffs(),
            new AddBuff(buff2)
        );
    }

    @Test
    void onFighterDieWithoutBuff() {
        requestStack.clear();

        listener.on(new FighterDie(other.fighter(), other.fighter()));
        requestStack.assertEmpty();
    }

    @Test
    void functional() {
        fight.dispatcher().add(listener);

        FightBuff buff1 = new FightBuff(Mockito.mock(SpellEffect.class), Mockito.mock(Spell.class), other.fighter(), player.fighter(), Mockito.mock(BuffHook.class));
        FightBuff buff2 = new FightBuff(Mockito.mock(SpellEffect.class), Mockito.mock(Spell.class), player.fighter(), player.fighter(), Mockito.mock(BuffHook.class));
        FightBuff buff3 = new FightBuff(Mockito.mock(SpellEffect.class), Mockito.mock(Spell.class), other.fighter(), player.fighter(), Mockito.mock(BuffHook.class));

        player.fighter().buffs().add(buff1);
        player.fighter().buffs().add(buff2);
        player.fighter().buffs().add(buff3);

        other.fighter().life().kill(player.fighter());

        assertIterableEquals(Collections.singletonList(buff2), player.fighter().buffs());
    }
}
