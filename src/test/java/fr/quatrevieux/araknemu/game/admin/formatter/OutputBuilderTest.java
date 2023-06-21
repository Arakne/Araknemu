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

import fr.quatrevieux.araknemu.network.game.out.basic.Noop;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OutputBuilderTest {
    @Test
    void append() {
        OutputBuilder builder = new OutputBuilder();

        assertEquals(
            "hello world text BN",
            builder
                .append("hello", " ", "world")
                .append(" text ")
                .append(new Noop())
                .toString()
        );
    }

    @Test
    void line() {
        OutputBuilder builder = new OutputBuilder();

        assertEquals(
            "aaa\n" +
            "bbb\n" +
            "ccc",
            builder
                .append("aaa")
                .line("bbb")
                .line("ccc")
                .toString()
        );
    }

    @Test
    void title() {
        OutputBuilder builder = new OutputBuilder();

        assertEquals(
            "aaa\n\n" +
            "<b>bbb</b>",
            builder
                .append("aaa")
                .title("bbb")
                .toString()
        );
    }

    @Test
    void indent() {
        OutputBuilder builder = new OutputBuilder();

        assertEquals(
            "aaa\n" +
            "\tbbb\n" +
            "\t\t\tccc\n" +
            "\t\tddd\n" +
            "\t\tddd",
            builder
                .append("aaa")
                .indent("bbb")
                .indent("ccc", 3)
                .indent("ddd\nddd", 2)
                .toString()
        );
    }

    @Test
    void variables() {
        OutputBuilder builder = new OutputBuilder();

        builder.with("name", () -> "World").append("Hello {{name}} !");

        assertEquals("Hello World !", builder.toString());

        builder
            .with("name", () -> "John")
            .with("age", () -> "42")
            .title("Presentation of {{name}}")
            .indent("My name is {{name}}")
            .line("I'm {{age}} y-o")
        ;

        assertEquals("Hello World !\n" +
            "\n" +
            "<b>Presentation of John</b>\n" +
            "\tMy name is John\n" +
            "I'm 42 y-o", builder.toString());
    }
}
