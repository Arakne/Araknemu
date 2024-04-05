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
 * Copyright (c) 2017-2024 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.core.config.env;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExpressionParserTest {
    @Test
    void evaluate() {
        ContextStub context = new ContextStub()
            .set("FOO", "bar")
            .set("NAME", "John")
        ;

        assertEquals("Hello World !", ExpressionParser.evaluate("Hello World !", context));
        assertEquals("", ExpressionParser.evaluate("$NOT_FOUND", context));
        assertEquals("bar", ExpressionParser.evaluate("$FOO", context));
        assertEquals("Hello John!", ExpressionParser.evaluate("Hello $NAME!", context));
        assertEquals("Hello John!", ExpressionParser.evaluate("Hello ${NAME}!", context));
        assertEquals("Hello World!", ExpressionParser.evaluate("Hello ${NOT_FOUND:World}!", context));
        assertEquals("Hello World!", ExpressionParser.evaluate("Hello ${NOT_FOUND:-World}!", context));
        assertEquals("Hello   World  !", ExpressionParser.evaluate("Hello ${NOT_FOUND:  World  }!", context));
        assertEquals("Johnbar", ExpressionParser.evaluate("$NAME$FOO$NOT_FOUND", context));
        assertEquals("", ExpressionParser.evaluate("${ NAME }", context));
        assertEquals("{NAME}", ExpressionParser.evaluate("$\\{NAME}", context));
        assertEquals("${NAME}", ExpressionParser.evaluate("\\${NAME}", context));
        assertEquals("John$FOO", ExpressionParser.evaluate("$NAME\\$FOO", context));
        assertEquals("150$", ExpressionParser.evaluate("150$", context));
        assertEquals("$\\", ExpressionParser.evaluate("\\$\\\\", context));
        assertEquals("test :", ExpressionParser.evaluate("test ${NOT_FOUND::}", context));
        assertEquals("foo : bar", ExpressionParser.evaluate("   foo : $FOO   ", context));
        assertEquals(" foo : bar ", ExpressionParser.evaluate("  \" foo : $FOO \"  ", context));
    }

    class ContextStub implements ExpressionParser.Context {
        public Map<String, String> entries = new HashMap<>();

        public ContextStub set(String key, String value){
            entries.put(key, value);
            return this;
        }

        @Override
        public String get(String varName, String defaultValue) {
            return entries.getOrDefault(varName, defaultValue);
        }
    }
}
