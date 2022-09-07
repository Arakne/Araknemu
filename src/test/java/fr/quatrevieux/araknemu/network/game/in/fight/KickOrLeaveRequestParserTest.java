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
 * Copyright (c) 2017-2021 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.network.game.in.fight;

import fr.quatrevieux.araknemu._test.TestCase;
import fr.quatrevieux.araknemu.core.network.parser.Packet;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class KickOrLeaveRequestParserTest extends TestCase {
    @Test
    void kick() {
        Packet p = new KickOrLeaveRequestParser().parse("123");

        assertInstanceOf(KickFighterRequest.class, p);
        assertEquals(123, ((KickFighterRequest) p).fighterId());
    }

    @Test
    void leave() {
        assertInstanceOf(LeaveFightRequest.class, new KickOrLeaveRequestParser().parse(""));
    }
}
