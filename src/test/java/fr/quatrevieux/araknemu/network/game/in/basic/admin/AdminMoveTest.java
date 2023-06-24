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

package fr.quatrevieux.araknemu.network.game.in.basic.admin;

import fr.quatrevieux.araknemu.core.network.parser.ParsePacketException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AdminMoveTest {
    private AdminMove.Parser parser = new AdminMove.Parser();

    @Test
    void success() {
        AdminMove move = parser.parse("-4,5");

        assertEquals(-4, move.geolocation().x());
        assertEquals(5, move.geolocation().y());
    }

    @Test
    void badFormat() {
        assertThrows(ParsePacketException.class, () -> parser.parse("invalid"));
    }
}
