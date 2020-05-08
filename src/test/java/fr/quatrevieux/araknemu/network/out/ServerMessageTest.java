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

package fr.quatrevieux.araknemu.network.out;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ServerMessageTest {
    @Test
    void displayNow() {
        assertEquals("M112|1;2", new ServerMessage(true, 12, new Object[] {1, 2}, null).toString());
    }

    @Test
    void displayOnLogout() {
        assertEquals("M012|1;2", new ServerMessage(false, 12, new Object[] {1, 2}, null).toString());
    }

    @Test
    void withName() {
        assertEquals("M112|1;2|name", new ServerMessage(true, 12, new Object[] {1, 2}, "name").toString());
    }

    @Test
    void notEnoughKamasForBank() {
        assertEquals("M110|123", ServerMessage.notEnoughKamasForBank(123).toString());
    }

    @Test
    void inactivity() {
        assertEquals("M01|", ServerMessage.inactivity().toString());
    }

    @Test
    void shutdown() {
        assertEquals("M04|", ServerMessage.shutdown().toString());
    }
}
