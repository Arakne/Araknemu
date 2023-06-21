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

package fr.quatrevieux.araknemu.game.fight.castable.effect;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ElementTest {
    @Test
    void fromBitSet() {
        assertEquals(Collections.emptySet(), Element.fromBitSet(0));
        assertEquals(EnumSet.of(Element.NEUTRAL), Element.fromBitSet(1));
        assertEquals(EnumSet.of(Element.EARTH), Element.fromBitSet(2));
        assertEquals(EnumSet.of(Element.WATER), Element.fromBitSet(4));
        assertEquals(EnumSet.of(Element.AIR), Element.fromBitSet(8));
        assertEquals(EnumSet.of(Element.FIRE), Element.fromBitSet(16));
        assertEquals(EnumSet.of(Element.AIR, Element.NEUTRAL), Element.fromBitSet(9));
        assertSame(Element.fromBitSet(9), Element.fromBitSet(9));
    }

    @Test
    void physical() {
        assertTrue(Element.NEUTRAL.physical());
        assertTrue(Element.EARTH.physical());
        assertFalse(Element.WATER.physical());
        assertFalse(Element.AIR.physical());
        assertFalse(Element.FIRE.physical());
    }
}