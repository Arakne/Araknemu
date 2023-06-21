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

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.data.value.BoostStatsData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BoostStatsDataTransformerTest {
    @Test
    void invalidCalls() {
        assertThrows(IllegalArgumentException.class, () -> new BoostStatsDataTransformer().unserialize(null));
        assertThrows(IllegalArgumentException.class, () -> new BoostStatsDataTransformer().unserialize(""));
        assertThrows(UnsupportedOperationException.class, () -> new BoostStatsDataTransformer().serialize(null));
    }

    @Test
    void unserialize() {
        String data = "10:0@2,50@3,150@4,250@5;11:0@1;12:0@3;13:0@1,20@2,40@3,60@4,80@5;14:0@1,20@2,40@3,60@4,80@5;15:0@1,100@2,200@3,300@4,400@5";

        BoostStatsData boost = new BoostStatsDataTransformer().unserialize(data);

        assertEquals(2, boost.get(Characteristic.STRENGTH, 15).cost());
        assertEquals(3, boost.get(Characteristic.STRENGTH, 78).cost());
        assertEquals(4, boost.get(Characteristic.STRENGTH, 153).cost());
        assertEquals(5, boost.get(Characteristic.STRENGTH, 300).cost());

        assertEquals(1, boost.get(Characteristic.VITALITY, 223).cost());

        assertEquals(3, boost.get(Characteristic.WISDOM, 123).cost());

        assertEquals(1, boost.get(Characteristic.LUCK, 15).cost());
        assertEquals(2, boost.get(Characteristic.LUCK, 22).cost());
        assertEquals(3, boost.get(Characteristic.LUCK, 42).cost());
        assertEquals(4, boost.get(Characteristic.LUCK, 62).cost());
        assertEquals(5, boost.get(Characteristic.LUCK, 82).cost());

        assertEquals(1, boost.get(Characteristic.AGILITY, 15).cost());
        assertEquals(2, boost.get(Characteristic.AGILITY, 22).cost());
        assertEquals(3, boost.get(Characteristic.AGILITY, 42).cost());
        assertEquals(4, boost.get(Characteristic.AGILITY, 62).cost());
        assertEquals(5, boost.get(Characteristic.AGILITY, 82).cost());

        assertEquals(1, boost.get(Characteristic.INTELLIGENCE, 88).cost());
        assertEquals(2, boost.get(Characteristic.INTELLIGENCE, 188).cost());
        assertEquals(3, boost.get(Characteristic.INTELLIGENCE, 288).cost());
        assertEquals(4, boost.get(Characteristic.INTELLIGENCE, 388).cost());
        assertEquals(5, boost.get(Characteristic.INTELLIGENCE, 400).cost());
    }
}
