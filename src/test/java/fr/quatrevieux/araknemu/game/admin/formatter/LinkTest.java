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

package fr.quatrevieux.araknemu.game.admin.formatter;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LinkTest {
    @Test
    void command() {
        assertEquals("<u><a href='asfunction:onHref,ExecCmd,my command,false'>my link</a></u>", new Link().text("my link").command("my command").toString());
    }

    @Test
    void execute() {
        assertEquals("<u><a href='asfunction:onHref,ExecCmd,my command,true'>my link</a></u>", new Link().text("my link").execute("my command").toString());
    }

    @Test
    void playerMenu() {
        assertEquals("<u><a href='asfunction:onHref,ShowPlayerPopupMenu,john'>my link</a></u>", new Link().text("my link").playerMenu("john").toString());
    }

    @Test
    void arguments() {
        assertEquals("<u><a href='asfunction:onHref,arg1,arg2'>my link</a></u>", new Link().text("my link").arguments("arg1", "arg2").toString());
        assertEquals("<u><a href='asfunction:onHref,arg%2Cwith%2Ccomma'>my link</a></u>", new Link().text("my link").arguments("arg,with,comma").toString());
    }

    @Test
    void linkTypes() {
        assertEquals("<u><a href='asfunction:onHref,ExecCmd,command,false'>command</a></u>", Link.Type.WRITE.create("command").toString());
        assertEquals("<u><a href='asfunction:onHref,ExecCmd,command,true'>command</a></u>", Link.Type.EXECUTE.create("command").toString());
        assertEquals("<u><a href='asfunction:onHref,ExecCmd,help command,true'>command</a></u>", Link.Type.HELP.create("command").toString());
        assertEquals("<u><a href='asfunction:onHref,ShowPlayerPopupMenu,player'>player</a></u>", Link.Type.PLAYER.create("player").toString());
    }

    @Test
    void invalid() {
        assertThrows(IllegalStateException.class, () -> new Link().toString());
        assertThrows(IllegalStateException.class, () -> new Link().text("foo").toString());
        assertThrows(IllegalStateException.class, () -> new Link().arguments("foo").toString());
    }
}
