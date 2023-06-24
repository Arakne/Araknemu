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

package fr.quatrevieux.araknemu.data.constant;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CharacteristicTest {
    @Test
    void fromId() {
        assertEquals(Characteristic.STRENGTH, Characteristic.fromId(10));
        assertEquals(Characteristic.VITALITY, Characteristic.fromId(11));
        assertEquals(Characteristic.WISDOM, Characteristic.fromId(12));
        assertEquals(Characteristic.LUCK, Characteristic.fromId(13));
        assertEquals(Characteristic.AGILITY, Characteristic.fromId(14));
        assertEquals(Characteristic.INTELLIGENCE, Characteristic.fromId(15));
    }

    @Test
    void id() {
        assertEquals(10, Characteristic.STRENGTH.id());
        assertEquals(11, Characteristic.VITALITY.id());
        assertEquals(12, Characteristic.WISDOM.id());
        assertEquals(13, Characteristic.LUCK.id());
        assertEquals(14, Characteristic.AGILITY.id());
        assertEquals(15, Characteristic.INTELLIGENCE.id());
    }
}
