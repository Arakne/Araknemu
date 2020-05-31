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

class LogFormatterTest {
    @Test
    void format() {
        assertEquals("Hello World !", LogFormatter.format("Hello World !"));
        assertEquals("Hello John !", LogFormatter.format("Hello {} !", "John"));
        assertEquals("Foo Bar", LogFormatter.format("{} {}", "Foo", "Bar"));
        assertEquals("", LogFormatter.format(""));
        assertEquals("abcd", LogFormatter.format("{}{}{}{}", 'a', 'b', 'c', 'd'));
        assertThrows(IndexOutOfBoundsException.class, () -> LogFormatter.format("Hello {} !"));
    }
}
