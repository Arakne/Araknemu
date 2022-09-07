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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MapCellsTransformerTest {
    private MapCellsTransformer transformer;

    @BeforeEach
    void setUp() {
        transformer = new MapCellsTransformer();
    }

    @Test
    void unserializeNull() {
        assertNull(transformer.unserialize(null));
    }

    @Test
    void serialize() {
        assertThrows(UnsupportedOperationException.class, () -> transformer.serialize(null));
    }

    @Test
    void unserializeSameDataWillReturnSameObject() {
        assertArrayEquals(
            transformer.unserialize("Hhaaeaaaaa"),
            transformer.unserialize("Hhaaeaaaaa")
        );
    }
}
