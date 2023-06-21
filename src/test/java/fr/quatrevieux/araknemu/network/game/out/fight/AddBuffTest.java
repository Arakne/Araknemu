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

package fr.quatrevieux.araknemu.network.game.out.fight;

import fr.quatrevieux.araknemu.game.fight.castable.Castable;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buff;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.BuffHook;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AddBuffTest {
    @Test
    void generate() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Fighter fighter = Mockito.mock(Fighter.class);
        Spell spell = Mockito.mock(Spell.class);

        Mockito.when(effect.effect()).thenReturn(99);
        Mockito.when(effect.duration()).thenReturn(3);
        Mockito.when(effect.min()).thenReturn(5);
        Mockito.when(effect.max()).thenReturn(7);
        Mockito.when(effect.text()).thenReturn("");
        Mockito.when(fighter.id()).thenReturn(123);
        Mockito.when(spell.id()).thenReturn(456);

        Buff buff = new Buff(effect, spell, fighter, fighter, Mockito.mock(BuffHook.class));

        assertEquals("GIE99;123;5;7;0;;3;456", new AddBuff(buff).toString());
    }

    @Test
    void generateWithoutMax() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Fighter fighter = Mockito.mock(Fighter.class);
        Spell spell = Mockito.mock(Spell.class);

        Mockito.when(effect.effect()).thenReturn(99);
        Mockito.when(effect.duration()).thenReturn(3);
        Mockito.when(effect.min()).thenReturn(5);
        Mockito.when(effect.text()).thenReturn("");
        Mockito.when(fighter.id()).thenReturn(123);
        Mockito.when(spell.id()).thenReturn(456);

        Buff buff = new Buff(effect, spell, fighter, fighter, Mockito.mock(BuffHook.class));

        assertEquals("GIE99;123;5;;0;;3;456", new AddBuff(buff).toString());
    }

    @Test
    void generateWithoutSpell() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Fighter fighter = Mockito.mock(Fighter.class);
        Castable castable = Mockito.mock(Castable.class);

        Mockito.when(effect.effect()).thenReturn(99);
        Mockito.when(effect.duration()).thenReturn(3);
        Mockito.when(effect.min()).thenReturn(5);
        Mockito.when(effect.text()).thenReturn("");
        Mockito.when(fighter.id()).thenReturn(123);

        Buff buff = new Buff(effect, castable, fighter, fighter, Mockito.mock(BuffHook.class));

        assertEquals("GIE99;123;5;;0;;3;", new AddBuff(buff).toString());
    }
}