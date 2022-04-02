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

package fr.quatrevieux.araknemu.network.game.in.exchange.store;

import fr.quatrevieux.araknemu.core.network.parser.ParsePacketException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SellRequestTest {
    private SellRequest.Parser parser;

    @BeforeEach
    void setUp() {
        parser = new SellRequest.Parser();
    }

    @Test
    void parseSuccess() {
        SellRequest request = parser.parse("112|3");

        assertEquals(112, request.itemId());
        assertEquals(3, request.quantity());
    }

    @Test
    void parseInvalid() {
        assertThrows(ParsePacketException.class, () -> parser.parse("invalid"));
    }

    @Test
    void parseNegativeQuantity() {
        assertThrows(ParsePacketException.class, () -> parser.parse("112|-3"));
    }
}
