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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ExchangeItemsTransformerTest {
    private ExchangeItemsTransformer transformer;

    @BeforeEach
    void setUp() {
        transformer = new ExchangeItemsTransformer();
    }

    @Test
    void unserialize() {
        assertEquals(new HashMap<>(), transformer.unserialize(""));
        assertEquals(new HashMap<Integer, Integer>() {{ put(123, 1); }}, transformer.unserialize("123"));
        assertEquals(new HashMap<Integer, Integer>() {{ put(123, 4); }}, transformer.unserialize("123:4"));
        assertEquals(new HashMap<Integer, Integer>() {{ put(123, 4); put(567, 1); }}, transformer.unserialize("123:4;567"));
    }

    @Test
    void serialize() {
        assertThrows(UnsupportedOperationException.class, () -> transformer.serialize(new HashMap<>()));
    }
}
