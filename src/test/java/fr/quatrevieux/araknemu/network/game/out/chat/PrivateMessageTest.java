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

package fr.quatrevieux.araknemu.network.game.out.chat;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PrivateMessageTest extends GameBaseCase {
    @Test
    void generate() throws SQLException, ContainerException {
        assertEquals(
            "cMKF|1|Bob|Hello World !|my items",
            new PrivateMessage(
                PrivateMessage.TYPE_FROM,
                gamePlayer(),
                "Hello World !",
                "my items"
            ).toString()
        );
    }

    @Test
    void generateWithHtmlChars() throws SQLException, ContainerException {
        assertEquals(
            "cMKF|1|Bob|&lt;a href='test'&gt;my link&lt;a/&gt;|+&amp;",
            new PrivateMessage(
                PrivateMessage.TYPE_FROM,
                gamePlayer(),
                "<a href='test'>my link<a/>",
                "||+&||"
            ).toString()
        );
    }

    @Test
    void generateDoNotDoubleEscapeLtAndGt() throws SQLException, ContainerException {
        assertEquals(
            "cMKF|1|Bob|&lt;b&gt;test&lt;/b&gt;|",
            new PrivateMessage(
                PrivateMessage.TYPE_FROM,
                gamePlayer(),
                "&lt;b&gt;test&lt;/b&gt;",
                ""
            ).toString()
        );
    }
}
