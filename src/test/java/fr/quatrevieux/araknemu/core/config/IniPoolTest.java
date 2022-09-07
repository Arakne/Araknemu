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

package fr.quatrevieux.araknemu.core.config;

import org.apache.commons.lang3.tuple.Pair;
import org.ini4j.Ini;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class IniPoolTest {
    private IniPool pool;

    @BeforeEach
    void setUp() throws IOException {
        pool = new IniPool(
            new Ini(new File("src/test/test_config.ini")).get("realm")
        );
    }

    @Test
    void has() {
        assertTrue(pool.has("server.port"));
        assertFalse(pool.has("not_found"));
    }

    @Test
    void get() {
        assertEquals("456", pool.get("server.port"));
        assertEquals("1.29.1", pool.get("client.version"));
    }

    @Test
    void getAll() {
        assertEquals(Arrays.asList("foo", "bar", "baz"), pool.getAll("multivalues"));
        assertEquals(Collections.emptyList(), pool.getAll("not_found"));
    }

    @Test
    void iterator() {
        assertIterableEquals(
            Arrays.asList(
                Pair.of("server.port", "456"),
                Pair.of("client.version", "1.29.1"),
                Pair.of("multivalues", "foo"),
                Pair.of("multivalues", "bar"),
                Pair.of("multivalues", "baz")
            ),
            pool
        );
    }
}
