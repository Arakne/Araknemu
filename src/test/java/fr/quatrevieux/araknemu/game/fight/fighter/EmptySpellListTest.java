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
 * Copyright (c) 2017-2023 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.fighter;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EmptySpellListTest {
    @Test
    void get() {
        assertThrows(NoSuchElementException.class, () -> EmptySpellList.INSTANCE.get(0));
    }

    @Test
    void has() {
        assertFalse(EmptySpellList.INSTANCE.has(0));
    }

    @Test
    void iterator() {
        assertIterableEquals(EmptySpellList.INSTANCE, new ArrayList<>());
    }

    @Test
    void boost() {
        EmptySpellList.INSTANCE.boost(0, null, 0); // do nothing
    }
}
