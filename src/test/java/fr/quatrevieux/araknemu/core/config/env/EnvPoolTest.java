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

import fr.quatrevieux.araknemu.core.config.IniPool;
import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvEntry;
import org.ini4j.Ini;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EnvPoolTest {
    @Test
    void get() throws IOException {
        DotenvStub dotenv = new DotenvStub()
            .set("FOO", "bar")
            .set("NAME", "John")
        ;

        EnvPool pool = new EnvPool(new IniPool(
            new Ini(new File("src/test/test_config_env.ini")).get("test")
        ), dotenv);

        assertEquals("Hello John!", pool.get("message"));
        assertSame(pool.get("message"), pool.get("message"));
        assertEquals("bar", pool.get("foo"));
        assertEquals("test ???", pool.get("with_default"));
        assertEquals("raw string", pool.get("raw"));
        assertNull(pool.get("not_found"));
    }

    @Test
    void has() throws IOException {
        DotenvStub dotenv = new DotenvStub()
            .set("FOO", "bar")
            .set("NAME", "John")
        ;

        EnvPool pool = new EnvPool(new IniPool(
            new Ini(new File("src/test/test_config_env.ini")).get("test")
        ), dotenv);

        assertTrue(pool.has("message"));
        assertFalse(pool.has("not_found"));
    }

    @Test
    void getAll() throws IOException {
        DotenvStub dotenv = new DotenvStub()
            .set("FOO", "bar")
            .set("NAME", "John")
        ;

        EnvPool pool = new EnvPool(new IniPool(
            new Ini(new File("src/test/test_config_env.ini")).get("test")
        ), dotenv);

        assertEquals(Arrays.asList("foo: bar", "bar: ???", "baz: ???"), pool.getAll("multivalues"));
    }

    @Test
    void iterator() throws IOException {
        DotenvStub dotenv = new DotenvStub()
            .set("FOO", "bar")
            .set("NAME", "John")
        ;

        EnvPool pool = new EnvPool(new IniPool(
            new Ini(new File("src/test/test_config_env.ini")).get("test")
        ), dotenv);

        List<String> entries = new ArrayList<>();

        pool.forEach(e -> entries.add(e.getKey() + ": " + e.getValue()));

        assertEquals(Arrays.asList(
            "message: Hello John!",
            "foo: bar",
            "with_default: test ???",
            "raw: raw string",
            "multivalues: foo: bar",
            "multivalues: bar: ???",
            "multivalues: baz: ???"
        ), entries);
    }

    class DotenvStub implements Dotenv {
        public Map<String, String> entries = new HashMap<>();

        public DotenvStub set(String key, String value){
            entries.put(key, value);
            return this;
        }

        @Override
        public Set<DotenvEntry> entries() {
            return entries.entrySet().stream().map(e -> new DotenvEntry(e.getKey(), e.getValue())).collect(Collectors.toSet());
        }

        @Override
        public Set<DotenvEntry> entries(Filter filter) {
            return entries();
        }

        @Override
        public String get(String key) {
            return entries.get(key);
        }

        @Override
        public String get(String key, String defaultValue) {
            return entries.getOrDefault(key, defaultValue);
        }
    }
}
