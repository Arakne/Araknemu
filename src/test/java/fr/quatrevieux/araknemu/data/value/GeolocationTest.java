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
 * Copyright (c) 2017-2022 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.data.value;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class GeolocationTest {
    @Test
    void equal() {
        Geolocation geolocation = new Geolocation(1, 3);

        assertEquals(geolocation, geolocation);
        assertEquals(new Geolocation(1, 3), geolocation);

        assertNotEquals(new Geolocation(1, 2), geolocation);
        assertNotEquals(new Geolocation(0, 3), geolocation);
        assertNotEquals(new Geolocation(0, 0), geolocation);
        assertNotEquals(geolocation, null);
        assertNotEquals(geolocation, new Object());
    }

    @Test
    void hash() {
        Geolocation geolocation = new Geolocation(1, 3);

        assertEquals(geolocation.hashCode(), geolocation.hashCode());
        assertEquals(geolocation.hashCode(), new Geolocation(1, 3).hashCode());

        assertNotEquals(new Geolocation(1, 2).hashCode(), geolocation.hashCode());
    }

    @Test
    void string() {
        assertEquals("[1,3]", new Geolocation(1, 3).toString());
    }
}
