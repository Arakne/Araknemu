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

package fr.quatrevieux.araknemu.game.world.creature.characteristics;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class DefaultCharacteristicsTest {
    @Test
    void getSet() {
        DefaultCharacteristics characteristics = new DefaultCharacteristics();

        characteristics.set(Characteristic.INTELLIGENCE, 5);

        assertEquals(5, characteristics.get(Characteristic.INTELLIGENCE));
    }

    @Test
    void getDefault() {
        assertEquals(0, new DefaultCharacteristics().get(Characteristic.ACTION_POINT));
    }

    @Test
    void equalsBadInstance() {
        assertFalse(new DefaultCharacteristics().equals(new ArrayList<>()));
    }

    @Test
    void equalsTwoEmptyInstances() {
        assertEquals(
            new DefaultCharacteristics(),
            new DefaultCharacteristics()
        );
    }

    @Test
    void equalsSameInstance() {
        Characteristics c = new DefaultCharacteristics();

        assertEquals(c, c);
    }

    @Test
    void equalsNotSameValue() {
        DefaultCharacteristics c1 = new DefaultCharacteristics();
        c1.set(Characteristic.ACTION_POINT, 3);

        DefaultCharacteristics c2 = new DefaultCharacteristics();
        c2.set(Characteristic.ACTION_POINT, 5);

        assertFalse(c1.equals(c2));
    }

    @Test
    void equalsWithSameValues() {
        DefaultCharacteristics c1 = new DefaultCharacteristics();

        c1.set(Characteristic.ACTION_POINT, 3);
        c1.set(Characteristic.RESISTANCE_ACTION_POINT, 120);

        DefaultCharacteristics c2 = new DefaultCharacteristics();

        c2.set(Characteristic.ACTION_POINT, 3);
        c2.set(Characteristic.RESISTANCE_ACTION_POINT, 120);

        assertEquals(c1, c2);
    }

    @Test
    void equalsWillIgnoreNullValues() {
        DefaultCharacteristics c1 = new DefaultCharacteristics();

        c1.set(Characteristic.ACTION_POINT, 3);
        c1.set(Characteristic.RESISTANCE_ACTION_POINT, 120);
        c1.set(Characteristic.MAX_SUMMONED_CREATURES, 0);
        c1.set(Characteristic.STRENGTH, 0);

        DefaultCharacteristics c2 = new DefaultCharacteristics();

        c2.set(Characteristic.ACTION_POINT, 3);
        c2.set(Characteristic.RESISTANCE_ACTION_POINT, 120);

        assertEquals(c1, c2);
    }

    @Test
    void hashCodeEmptyCharacteristics() {
        assertEquals(0, new DefaultCharacteristics().hashCode());
    }

    @Test
    void hashCodeEquals() {
        DefaultCharacteristics c1 = new DefaultCharacteristics();

        c1.set(Characteristic.ACTION_POINT, 3);
        c1.set(Characteristic.RESISTANCE_ACTION_POINT, 120);
        c1.set(Characteristic.MAX_SUMMONED_CREATURES, 0);
        c1.set(Characteristic.STRENGTH, 0);

        DefaultCharacteristics c2 = new DefaultCharacteristics();

        c2.set(Characteristic.ACTION_POINT, 3);
        c2.set(Characteristic.RESISTANCE_ACTION_POINT, 120);

        assertEquals(c1.hashCode(), c2.hashCode());
    }

    @Test
    void hashCodeMayBeDifferentForNotEqualsValues() {
        DefaultCharacteristics c1 = new DefaultCharacteristics();
        c1.set(Characteristic.ACTION_POINT, 3);

        DefaultCharacteristics c2 = new DefaultCharacteristics();
        c2.set(Characteristic.ACTION_POINT, 5);

        assertNotEquals(
            c1.hashCode(),
            c2.hashCode()
        );
    }

    @Test
    void addFromNotSetValue() {
        DefaultCharacteristics c = new DefaultCharacteristics();

        c.add(Characteristic.INTELLIGENCE, 20);

        assertEquals(20, c.get(Characteristic.INTELLIGENCE));
    }

    @Test
    void addFromSetValue() {
        DefaultCharacteristics c = new DefaultCharacteristics();
        c.set(Characteristic.INTELLIGENCE, 50);

        c.add(Characteristic.INTELLIGENCE, 20);

        assertEquals(70, c.get(Characteristic.INTELLIGENCE));
    }
}
