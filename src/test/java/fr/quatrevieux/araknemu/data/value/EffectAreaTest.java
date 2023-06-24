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

package fr.quatrevieux.araknemu.data.value;

import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EffectAreaTest {
    @Test
    void equalsSameObjects() {
        EffectArea area = new EffectArea(EffectArea.Type.CIRCLE, 3);

        assertTrue(area.equals(area));
    }

    @Test
    void equalsNull() {
        EffectArea area = new EffectArea(EffectArea.Type.CIRCLE, 3);

        assertFalse(area.equals(null));
    }

    @Test
    void equals() {
        EffectArea area = new EffectArea(EffectArea.Type.CIRCLE, 3);

        assertFalse(area.equals(new EffectArea(EffectArea.Type.RING, 3)));
        assertFalse(area.equals(new EffectArea(EffectArea.Type.CIRCLE, 4)));
        assertTrue(area.equals(new EffectArea(EffectArea.Type.CIRCLE, 3)));
    }

    @Test
    void hashCodeValue() {
        EffectArea area = new EffectArea(EffectArea.Type.CIRCLE, 3);

        assertEquals(area.hashCode(), area.hashCode());
        assertEquals(area.hashCode(), new EffectArea(EffectArea.Type.CIRCLE, 3).hashCode());
        assertNotEquals(area.hashCode(), new EffectArea(EffectArea.Type.RING, 3).hashCode());
        assertNotEquals(area.hashCode(), new EffectArea(EffectArea.Type.CIRCLE, 4).hashCode());
    }

    @Test
    void typeByChar() {
        assertSame(EffectArea.Type.RING, EffectArea.Type.byChar('O'));
        assertThrows(NoSuchElementException.class, () -> EffectArea.Type.byChar('_'));
    }
}
