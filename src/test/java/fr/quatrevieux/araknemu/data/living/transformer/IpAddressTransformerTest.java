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
 * Copyright (c) 2017-2020 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.data.living.transformer;

import inet.ipaddr.IPAddressString;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IpAddressTransformerTest {
    private IpAddressTransformer transformer = new IpAddressTransformer();

    @Test
    void serialize() {
        assertEquals("12.52.102.78", transformer.serialize(new IPAddressString("12.52.102.78")));
        assertEquals("12.52.102.78/24", transformer.serialize(new IPAddressString("12.52.102.78/24")));
        assertEquals("12.52.102.78/8", transformer.serialize(new IPAddressString("12.52.102.78/255.0.0.0")));
        assertEquals("1:2f:::", transformer.serialize(new IPAddressString("1:2f:::")));
        assertEquals("1:2f:::/64", transformer.serialize(new IPAddressString("1:2f:::/64")));
    }

    @Test
    void unserialize() {
        assertEquals(new IPAddressString("12.52.102.78"), transformer.unserialize("12.52.102.78"));
        assertEquals(new IPAddressString("12.52.102.78/24"), transformer.unserialize("12.52.102.78/24"));
        assertEquals(new IPAddressString("12.52.102.78/8"), transformer.unserialize("12.52.102.78/8"));
        assertEquals(new IPAddressString("1:2f:::"), transformer.unserialize("1:2f:::"));
        assertEquals(new IPAddressString("1:2f:::/64"), transformer.unserialize("1:2f:::/64"));
    }
}
