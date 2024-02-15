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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

class NullAccessoryTest {
    @Test
    void data() {
        NullAccessory accessory = NullAccessory.from(AccessoryType.HELMET);

        assertEquals(AccessoryType.HELMET, accessory.type());
        assertEquals(0, accessory.appearance());
        assertEquals(-1, accessory.itemType());
        assertEquals(0, accessory.frame());
        assertEquals("", accessory.toString());
    }

    @Test
    void from() {
        assertSame(NullAccessory.from(AccessoryType.HELMET), NullAccessory.from(AccessoryType.HELMET));
        assertSame(NullAccessory.from(AccessoryType.WEAPON), NullAccessory.from(AccessoryType.WEAPON));
        assertNotSame(NullAccessory.from(AccessoryType.HELMET), NullAccessory.from(AccessoryType.WEAPON));
    }
}