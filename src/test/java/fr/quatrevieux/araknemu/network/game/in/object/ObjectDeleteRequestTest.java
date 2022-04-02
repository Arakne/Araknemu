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

package fr.quatrevieux.araknemu.network.game.in.object;

import fr.quatrevieux.araknemu.core.network.parser.ParsePacketException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ObjectDeleteRequestTest {
    @Test
    void parse() {
        ObjectDeleteRequest request = new ObjectDeleteRequest.Parser().parse("12|5");

        assertEquals(12, request.id());
        assertEquals(5, request.quantity());
    }

    @Test
    void parseBadFormat() {
        assertThrows(ParsePacketException.class, () -> new ObjectDeleteRequest.Parser().parse("invalid"));
    }

    @Test
    void parseNegativeQuantity() {
        assertThrows(ParsePacketException.class, () -> new ObjectDeleteRequest.Parser().parse("12|-5"));
    }
}
