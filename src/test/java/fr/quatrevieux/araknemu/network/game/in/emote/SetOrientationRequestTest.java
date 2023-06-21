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

package fr.quatrevieux.araknemu.network.game.in.emote;

import fr.arakne.utils.maps.constant.Direction;
import fr.quatrevieux.araknemu.core.network.parser.ParsePacketException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SetOrientationRequestTest {
    private SetOrientationRequest.Parser parser;

    @BeforeEach
    void setUp() {
        parser = new SetOrientationRequest.Parser();
    }

    @Test
    void invalidDirection() {
        assertThrows(ParsePacketException.class, () -> parser.parse("9"));
    }

    @Test
    void parse() {
        assertEquals(Direction.SOUTH_EAST, parser.parse("1").orientation());
        assertEquals(Direction.WEST, parser.parse("4").orientation());
    }
}
