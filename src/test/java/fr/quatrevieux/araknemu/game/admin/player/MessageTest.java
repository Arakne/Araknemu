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

package fr.quatrevieux.araknemu.game.admin.player;

import fr.quatrevieux.araknemu.game.admin.CommandTestCase;
import fr.quatrevieux.araknemu.game.admin.exception.AdminException;
import fr.quatrevieux.araknemu.network.game.out.chat.ServerMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

class MessageTest extends CommandTestCase {
    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        command = new Message(gamePlayer());
    }

    @Test
    void simpleMessage() throws AdminException, SQLException {
        execute("msg", "Hello", "World", "!");

        requestStack.assertLast(new ServerMessage("Hello World !"));
    }

    @Test
    void withColor() throws AdminException, SQLException {
        execute("msg", "--color", "ff0000", "Hello", "World", "!");
        requestStack.assertLast(new ServerMessage("<font color=\"#ff0000\">Hello World !</font>"));

        execute("msg", "--color", "red", "Hello", "World", "!");
        requestStack.assertLast(new ServerMessage("<font color=\"#C10000\">Hello World !</font>"));

        execute("msg", "--color", "green", "Hello", "World", "!");
        requestStack.assertLast(new ServerMessage("<font color=\"#009900\">Hello World !</font>"));

        execute("msg", "--color", "blue", "Hello", "World", "!");
        requestStack.assertLast(new ServerMessage("<font color=\"#0066FF\">Hello World !</font>"));

        execute("msg", "--color", "orange", "Hello", "World", "!");
        requestStack.assertLast(new ServerMessage("<font color=\"#DD7700\">Hello World !</font>"));
    }

    @Test
    void markdown() throws AdminException, SQLException {
        execute("msg", "**Hello", "World", "!**");
        requestStack.assertLast(new ServerMessage("<b>Hello World !</b>"));

        execute("msg", "*Hello", "World", "!*");
        requestStack.assertLast(new ServerMessage("<i>Hello World !</i>"));

        execute("msg", "_Hello", "World", "!_");
        requestStack.assertLast(new ServerMessage("<i>Hello World !</i>"));

        execute("msg", "__Hello", "World", "!__");
        requestStack.assertLast(new ServerMessage("<u>Hello World !</u>"));

        execute("msg", "**___Hello", "World", "!___**");
        requestStack.assertLast(new ServerMessage("<b><i><u>Hello World !</u></i></b>"));

        execute("msg", "**aaa", "_bbb_", "ccc**", "*ddd*");
        requestStack.assertLast(new ServerMessage("<b>aaa <i>bbb</i> ccc</b> <i>ddd</i>"));

        execute("msg", "[my link](http://example.com)");
        requestStack.assertLast(new ServerMessage("<u><a href=\"http://example.com\">my link</a></u>"));

        execute("msg", "[***my link***](http://example.com)");
        requestStack.assertLast(new ServerMessage("<u><a href=\"http://example.com\"><i><b>my link</b></i></a></u>"));
    }

    @Test
    void noMd() throws AdminException, SQLException {
        execute("msg", "--no-md", "**Hello", "World", "!**");
        requestStack.assertLast(new ServerMessage("**Hello World !**"));
    }

    @Test
    void help() {
        assertHelp(
            "msg - Send a message to all connected players",
            "========================================",
            "SYNOPSIS",
                "\t@[player] msg [options] MESSAGE",
            "OPTIONS",
                "\tMESSAGE : The message to send.",
                    "\t\tIf --no-md option is not set, the message will be parsed as markdown format.",
                    "\t\tAvailable formats :",
                    "\t\t- *message* or _message_ for italic",
                    "\t\t- **message** for bold",
                    "\t\t- __message__ for underline",
                    "\t\t- [test](http://link) for display a clickable link",
                    "\t\t- Multiple formats can be applied like : ___**message**___ for italic + underline + bold",
                    "\t\tNote: The message must be the last argument, after all options",
                "\t--color : Define a color. The value can be an hexadecimal value, without #, in form RRGGBB, or one of the defined color : RED, GREEN, BLUE, ORANGE.",
                "\t--no-md : Disable markdown parsing on the message",
            "EXAMPLES",
                "\t@John msg Hello World ! - Send message \"Hello World\" to John",
                "\t@John msg --color red My import message - Send a message in red",
                "\t@John msg --color CD5C5C My message - Send a message in custom color",
                "\t@John msg --color blue **Note:** go to link : [My link](https://my-site.com/my-page) - Send a message using markdown",
            "PERMISSIONS",
                "\t[ACCESS]"
        );
    }
}
