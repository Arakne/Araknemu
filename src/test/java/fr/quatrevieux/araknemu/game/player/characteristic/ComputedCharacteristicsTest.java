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
import fr.quatrevieux.araknemu.game.world.creature.characteristics.Characteristics;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.DefaultCharacteristics;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.MutableCharacteristics;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ComputedCharacteristicsTest {
    @Test
    void computedPointResistance() {
        MutableCharacteristics inner = new DefaultCharacteristics();
        ComputedCharacteristics<Characteristics> c = new ComputedCharacteristics<>(inner);

        inner.set(Characteristic.WISDOM, 44);

        assertEquals(11, c.get(Characteristic.RESISTANCE_ACTION_POINT));
        assertEquals(11, c.get(Characteristic.RESISTANCE_MOVEMENT_POINT));

        inner.set(Characteristic.RESISTANCE_ACTION_POINT, 10);

        assertEquals(21, c.get(Characteristic.RESISTANCE_ACTION_POINT));
        assertEquals(11, c.get(Characteristic.RESISTANCE_MOVEMENT_POINT));
    }

    @Test
    void notComputedCharacteristic() {
        MutableCharacteristics inner = new DefaultCharacteristics();
        ComputedCharacteristics<Characteristics> c = new ComputedCharacteristics<>(inner);

        inner.set(Characteristic.STRENGTH, 44);

        assertEquals(44, c.get(Characteristic.STRENGTH));
    }

    @Test
    void equalityAndHashCode() {
        assertEquals(new ComputedCharacteristics<>(new DefaultCharacteristics()), new ComputedCharacteristics<>(new DefaultCharacteristics()));
        assertNotEquals(new ComputedCharacteristics<>(new DefaultCharacteristics()), new DefaultCharacteristics());

        ComputedCharacteristics<Characteristics> c = new ComputedCharacteristics<>(new DefaultCharacteristics());
        assertEquals(c, c);

        MutableCharacteristics inner = new DefaultCharacteristics();
        inner.set(Characteristic.STRENGTH, 10);

        assertEquals(new ComputedCharacteristics<>(inner), new ComputedCharacteristics<>(inner));
        assertNotEquals(new ComputedCharacteristics<>(inner), new ComputedCharacteristics<>(new DefaultCharacteristics()));

        assertNotEquals(new ComputedCharacteristics<>(inner), null);
        assertNotEquals(new ComputedCharacteristics<>(inner), new Object());

        assertEquals(
            new ComputedCharacteristics<>(inner).hashCode(),
            new ComputedCharacteristics<>(inner).hashCode()
        );
        assertNotEquals(
            new ComputedCharacteristics<>(inner).hashCode(),
            new ComputedCharacteristics<>(new DefaultCharacteristics()).hashCode()
        );
    }
}
