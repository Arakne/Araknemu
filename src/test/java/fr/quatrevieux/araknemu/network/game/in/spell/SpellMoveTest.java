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

package fr.quatrevieux.araknemu.network.game.in.spell;

import fr.quatrevieux.araknemu.core.network.parser.ParsePacketException;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SpellMoveTest extends GameBaseCase {
    @Test
    void parse() {
        SpellMove move = new SpellMove.Parser().parse("123|5");

        assertEquals(123, move.spellId());
        assertEquals(5, move.position());
    }

    @Test
    void parseInvalidPosition() {
        assertThrows(ParsePacketException.class, () -> new SpellMove.Parser().parse("123|64"));
        assertThrows(ParsePacketException.class, () -> new SpellMove.Parser().parse("123|-2"));
    }
}
