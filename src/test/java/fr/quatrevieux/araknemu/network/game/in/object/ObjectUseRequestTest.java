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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ObjectUseRequestTest {
    @Test
    void parseSelfUsage() {
        ObjectUseRequest request = new ObjectUseRequest.Parser().parse("123");

        assertEquals(123, request.objectId());
        assertFalse(request.isTarget());

        request = new ObjectUseRequest.Parser().parse("123|");

        assertEquals(123, request.objectId());
        assertFalse(request.isTarget());
    }

    @Test
    void parseWithTarget() {
        ObjectUseRequest request = new ObjectUseRequest.Parser().parse("123|45|325");

        assertEquals(123, request.objectId());
        assertEquals(45, request.target());
        assertEquals(325, request.cell());
        assertTrue(request.isTarget());
    }

    @Test
    void parseWithCell() {
        ObjectUseRequest request = new ObjectUseRequest.Parser().parse("123||325");

        assertEquals(123, request.objectId());
        assertEquals(0, request.target());
        assertEquals(325, request.cell());
        assertTrue(request.isTarget());
    }
}