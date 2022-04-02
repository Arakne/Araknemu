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

package fr.quatrevieux.araknemu.game.world.creature.accessory;

import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class AccessoryTypeTest {
    @Test
    void bySlot() {
        assertEquals(AccessoryType.WEAPON, AccessoryType.bySlot(1));
        assertEquals(AccessoryType.HELMET, AccessoryType.bySlot(6));
        assertEquals(AccessoryType.MANTLE, AccessoryType.bySlot(7));
        assertEquals(AccessoryType.PET, AccessoryType.bySlot(8));
        assertEquals(AccessoryType.SHIELD, AccessoryType.bySlot(15));

        assertThrows(NoSuchElementException.class, () -> AccessoryType.bySlot(0));
    }

    @Test
    void isAccessorySlot() {
        assertTrue(AccessoryType.isAccessorySlot(1));
        assertTrue(AccessoryType.isAccessorySlot(6));
        assertFalse(AccessoryType.isAccessorySlot(-1));
        assertFalse(AccessoryType.isAccessorySlot(0));
    }

    @Test
    void slots() {
        assertArrayEquals(
            new int[] {1, 6, 7, 8, 15},
            AccessoryType.slots()
        );
    }
}