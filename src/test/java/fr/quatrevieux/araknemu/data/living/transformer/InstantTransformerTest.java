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
 * Copyright (c) 2017-2020 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.data.living.transformer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class InstantTransformerTest {
    private InstantTransformer transformer;

    @BeforeEach
    void setUp() {
        transformer = new InstantTransformer();
    }

    @Test
    void withNull() {
        assertNull(transformer.serialize(null));
        assertNull(transformer.unserialize(null));
    }

    @Test
    void withDate() {
        final Instant instant = Instant.parse("2020-06-15T15:25:30.00Z");

        assertEquals("2020-06-15 15:25:30", transformer.serialize(instant));
        assertEquals(instant, transformer.unserialize("2020-06-15 15:25:30"));
    }
}
