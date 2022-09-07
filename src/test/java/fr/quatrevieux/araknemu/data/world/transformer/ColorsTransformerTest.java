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

package fr.quatrevieux.araknemu.data.world.transformer;

import fr.arakne.utils.value.Colors;
import fr.quatrevieux.araknemu.data.transformer.TransformerException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ColorsTransformerTest {
    private ColorsTransformer transformer;

    @BeforeEach
    void setUp() {
        transformer = new ColorsTransformer();
    }

    @Test
    void withNull() {
        assertNull(transformer.unserialize(null));
        assertNull(transformer.serialize(null));
    }

    @Test
    void serialize() {
        assertEquals("7b,1c8,315", transformer.serialize(new Colors(123, 456, 789)));
    }

    @Test
    void unserializeSimple() {
        Colors colors = transformer.unserialize("7b,1c8,315");

        assertEquals(123, colors.color1());
        assertEquals(456, colors.color2());
        assertEquals(789, colors.color3());
    }

    @Test
    void unserializeDefault() {
        assertSame(Colors.DEFAULT, transformer.unserialize("-1,-1,-1"));
    }

    @Test
    void unserializeWithOneDefault() {
        Colors colors = transformer.unserialize("-1,1c8,315");

        assertEquals(-1, colors.color1());
        assertEquals(456, colors.color2());
        assertEquals(789, colors.color3());
    }

    @Test
    void unserializeInvalid() {
        assertThrows(TransformerException.class, () -> transformer.unserialize("-1,1c8;456"));
    }
}
