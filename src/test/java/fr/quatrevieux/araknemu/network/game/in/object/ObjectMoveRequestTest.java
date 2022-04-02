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

class ObjectMoveRequestTest {
    private ObjectMoveRequest.Parser parser = new ObjectMoveRequest.Parser();

    @Test
    void generateWithoutQuantity() {
        ObjectMoveRequest request = parser.parse("5|2");

        assertEquals(5, request.id());
        assertEquals(2, request.position());
        assertEquals(1, request.quantity());
    }

    @Test
    void generateWithQuantity() {
        ObjectMoveRequest request = parser.parse("5|2|52");

        assertEquals(5, request.id());
        assertEquals(2, request.position());
        assertEquals(52, request.quantity());
    }

    @Test
    void generateWithNegativeQuantity() {
        assertThrows(ParsePacketException.class, () -> parser.parse("5|2|-2"));
    }
}
