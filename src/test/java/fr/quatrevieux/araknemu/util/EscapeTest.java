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

package fr.quatrevieux.araknemu.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EscapeTest {
    @Test
    void html() {
        assertEquals("Hello World !", Escape.html("Hello World !"));
        assertEquals("&lt;strong&gt;Hello World !&lt;/strong&gt;", Escape.html("<strong>Hello World !</strong>"));
        assertEquals("&lt;strong&gt;Hello World !&lt;/strong&gt;", Escape.html("&lt;strong&gt;Hello World !&lt;/strong&gt;"));
        assertEquals("&amp;&amp;", Escape.html("&&"));
    }

    @Test
    void url() {
        assertEquals("Hello+World+%21", Escape.url("Hello World !"));
    }
}
