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
 * Copyright (c) 2017-2024 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.data.transformer;

import fr.quatrevieux.araknemu.data.value.SpellTarget;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SpellTargetsTransformerTest {
    @Test
    void serialize() {
        assertThrows(UnsupportedOperationException.class, () -> new SpellTargetsTransformer().serialize(null));
    }

    @Test
    void unserializeNull() {
        assertNull(new SpellTargetsTransformer().unserialize(null));
    }

    @Test
    void unserializeEmpty() {
        assertArrayEquals(new SpellTarget[0], new SpellTargetsTransformer().unserialize(""));
    }

    @Test
    void unserializeSimple() {
        SpellTarget[] parsed = new SpellTargetsTransformer().unserialize("1");

        assertEquals(1, parsed.length);
        assertEquals(1, parsed[0].normal());
        assertEquals(1, parsed[0].critical());
    }

    @Test
    void unserializeMultiple() {

        SpellTarget[] parsed = new SpellTargetsTransformer().unserialize("1;4;0");

        assertEquals(3, parsed.length);
        assertEquals(1, parsed[0].normal());
        assertEquals(1, parsed[0].critical());
        assertEquals(4, parsed[1].normal());
        assertEquals(4, parsed[1].critical());
        assertEquals(0, parsed[2].normal());
        assertEquals(0, parsed[2].critical());
    }

    @Test
    void unserializeMultipleWithExplicitCritical() {

        SpellTarget[] parsed = new SpellTargetsTransformer().unserialize("1,5;4;0,1");

        assertEquals(3, parsed.length);
        assertEquals(1, parsed[0].normal());
        assertEquals(5, parsed[0].critical());
        assertEquals(4, parsed[1].normal());
        assertEquals(4, parsed[1].critical());
        assertEquals(0, parsed[2].normal());
        assertEquals(1, parsed[2].critical());
    }
}
