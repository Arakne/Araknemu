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
 * Copyright (c) 2017-2021 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.player.characteristic;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.DefaultCharacteristics;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.MutableCharacteristics;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MutableComputedCharacteristicsTest {
    @Test
    void alter() {
        MutableCharacteristics inner = new DefaultCharacteristics();
        MutableComputedCharacteristics c = new MutableComputedCharacteristics(inner);

        c.set(Characteristic.STRENGTH, 10);

        assertEquals(10, c.get(Characteristic.STRENGTH));
        assertEquals(10, inner.get(Characteristic.STRENGTH));

        c.add(Characteristic.STRENGTH, 5);

        assertEquals(15, c.get(Characteristic.STRENGTH));
        assertEquals(15, inner.get(Characteristic.STRENGTH));
    }

    @Test
    void computedPointResistance() {
        MutableComputedCharacteristics c = new MutableComputedCharacteristics(new DefaultCharacteristics());

        c.set(Characteristic.WISDOM, 44);

        assertEquals(11, c.get(Characteristic.RESISTANCE_ACTION_POINT));
        assertEquals(11, c.get(Characteristic.RESISTANCE_MOVEMENT_POINT));

        c.set(Characteristic.RESISTANCE_ACTION_POINT, 10);

        assertEquals(21, c.get(Characteristic.RESISTANCE_ACTION_POINT));
        assertEquals(11, c.get(Characteristic.RESISTANCE_MOVEMENT_POINT));
    }
}
