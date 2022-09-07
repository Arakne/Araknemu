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

package fr.quatrevieux.araknemu.network.game.in.account;

import fr.arakne.utils.value.constant.Gender;
import fr.arakne.utils.value.constant.Race;
import fr.quatrevieux.araknemu.core.network.parser.ParsePacketException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AddCharacterRequestTest {
    @Test
    void parseSuccess() {
        AddCharacterRequest.Parser parser = new AddCharacterRequest.Parser();

        AddCharacterRequest request = parser.parse("bob|5|1|145|541|123");

        assertEquals("bob", request.name());
        assertEquals(Race.XELOR, request.race());
        assertEquals(Gender.FEMALE, request.gender());
        assertEquals(145, request.colors().color1());
        assertEquals(541, request.colors().color2());
        assertEquals(123, request.colors().color3());
    }

    @Test
    void parseInvalidData() {
        AddCharacterRequest.Parser parser = new AddCharacterRequest.Parser();

        assertThrows(ParsePacketException.class, () -> parser.parse("invalid"));
    }

    @Test
    void parseInvalidRace() {
        AddCharacterRequest.Parser parser = new AddCharacterRequest.Parser();

        assertThrows(ParsePacketException.class, () -> parser.parse("bob|0|1|145|541|123"));
    }
    @Test
    void parseInvalidColor() {
        AddCharacterRequest.Parser parser = new AddCharacterRequest.Parser();

        assertThrows(ParsePacketException.class, () -> parser.parse("bob|5|1|-5|541|123"));
        assertThrows(ParsePacketException.class, () -> parser.parse("bob|5|1|1000000000|541|123"));
    }
}
