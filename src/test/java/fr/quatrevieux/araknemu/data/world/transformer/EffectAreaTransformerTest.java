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

import fr.quatrevieux.araknemu.data.value.EffectArea;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EffectAreaTransformerTest {
    private EffectAreaTransformer transformer = new EffectAreaTransformer();

    @Test
    void unserializeNull() {
        assertNull(transformer.unserialize(null));
    }

    @Test
    void unserializeInvalid() {
        assertThrows(IllegalArgumentException.class, () -> transformer.unserialize("a"));
    }

    @Test
    void unserialize() {
        assertEquals(new EffectArea(EffectArea.Type.CIRCLE, 3), transformer.unserialize("Cd"));
    }

    @Test
    void serializeNull() {
        assertNull(transformer.serialize(null));
    }

    @Test
    void serialize() {
        assertEquals("Cd", transformer.serialize(new EffectArea(EffectArea.Type.CIRCLE, 3)));
    }
}
