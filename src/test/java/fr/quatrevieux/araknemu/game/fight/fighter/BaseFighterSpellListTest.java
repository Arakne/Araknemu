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

package fr.quatrevieux.araknemu.game.fight.fighter;

import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.spell.boost.SpellsBoosts;
import org.apache.commons.lang3.stream.Streams;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.*;

class BaseFighterSpellListTest extends FightBaseCase {
    private BaseFighterSpellList spells;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        createFight();
        spells = new BaseFighterSpellList(player.fighter().spells());
    }

    @Test
    void get() {
        assertSame(player.properties().spells().get(3), spells.get(3));
        assertSame(player.properties().spells().get(6), spells.get(6));
    }

    @Test
    void has() {
        assertTrue(spells.has(3));
        assertTrue(spells.has(6));
        assertFalse(spells.has(404));
    }

    @Test
    void boost() {
        spells.boost(3, SpellsBoosts.Modifier.BASE_DAMAGE, 5);

        assertNotSame(player.properties().spells().get(3), spells.get(3));
        assertEquals(7, spells.get(3).effects().get(0).min());
        assertEquals(11, spells.get(3).effects().get(0).max());

        spells.boost(3, SpellsBoosts.Modifier.BASE_DAMAGE, -5);

        assertSame(player.properties().spells().get(3), spells.get(3));
        assertEquals(2, spells.get(3).effects().get(0).min());
        assertEquals(6, spells.get(3).effects().get(0).max());
    }

    @Test
    void iterator() {
        assertIterableEquals(Arrays.asList(
            player.properties().spells().get(17),
            player.properties().spells().get(3),
            player.properties().spells().get(6)
        ), spells);

        spells.boost(3, SpellsBoosts.Modifier.BASE_DAMAGE, 5);

        List<Spell> list = StreamSupport.stream(spells.spliterator(), false).collect(Collectors.toList());

        assertSame(player.properties().spells().get(17), list.get(0));
        assertSame(player.properties().spells().get(6), list.get(2));

        assertNotSame(player.properties().spells().get(3), list.get(1));
        assertEquals(7, list.get(1).effects().get(0).min());
        assertEquals(11, list.get(1).effects().get(0).max());
    }
}
