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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.ZoneId;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PoolUtilsTest {
    private PoolUtils pool;
    private Map<String, String> map = new HashMap<>();

    @BeforeEach
    void setUp() {
        map.put("test_bool", "yes");
        map.put("test_int", "774");
        map.put("test_string", "foo");
        map.put("test_double", "1.5");

        pool = new PoolUtils(
            new Pool() {
                @Override
                public boolean has(String key) {
                    return map.containsKey(key);
                }

                @Override
                public String get(String key) {
                    return map.get(key);
                }

                @Override
                public List<String> getAll(String key) {
                    return has(key) ? Collections.singletonList(get(key)) : Collections.emptyList();
                }

                @Override
                public Iterator<Map.Entry<String, String>> iterator() {
                    return map.entrySet().iterator();
                }
            }
        );
    }

    @Test
    void has() {
        assertTrue(pool.has("test_bool"));
        assertFalse(pool.has("not_found"));
    }

    @Test
    void get() {
        assertEquals("yes", pool.get("test_bool"));
    }

    @Test
    void getAll() {
        assertEquals(Collections.singletonList("yes"), pool.getAll("test_bool"));
    }

    @Test
    void iterator() {
        assertIterableEquals(
            map.entrySet(),
            pool
        );
    }

    @Test
    void integer() {
        assertEquals(774, pool.integer("test_int"));
        assertEquals(0, pool.integer("not_found"));

        assertEquals(774, pool.integer("test_int", 123));
        assertEquals(123, pool.integer("not_found", 123));
    }

    @Test
    void bool() {
        assertTrue(pool.bool("test_bool"));
        assertFalse(pool.bool("not_found"));

        assertTrue(pool.bool("test_bool", false));
        assertTrue(pool.bool("not_found", true));
    }

    @Test
    void string() {
        assertEquals("foo", pool.string("test_string"));
        assertEquals("", pool.string("not_found"));

        assertEquals("foo", pool.string("test_string", "bar"));
        assertEquals("bar", pool.string("not_found", "bar"));
    }

    @Test
    void decimal() {
        assertEquals(1.5, pool.decimal("test_double"));
        assertEquals(0, pool.decimal("not_found"));

        assertEquals(1.5, pool.decimal("test_double", 2));
        assertEquals(2, pool.decimal("not_found", 2));
    }

    @Test
    void duration() {
        map.put("simple", "15s");
        map.put("min_and_sec", "2m5s");
        map.put("with_prefix", "pt1h");
        map.put("date", "p1dt5h");

        assertEquals(Duration.ofSeconds(404), pool.duration("not_found", Duration.ofSeconds(404)));
        assertEquals(Duration.ofSeconds(15), pool.duration("simple"));
        assertEquals(Duration.ofSeconds(125), pool.duration("min_and_sec"));
        assertEquals(Duration.ofHours(1), pool.duration("with_prefix"));
        assertEquals(Duration.ofHours(29), pool.duration("date"));
    }

    @Test
    void zoneId() {
        map.put("test", "Europe/Paris");

        assertEquals("Europe/Paris", pool.zoneId("test").getId());
        assertEquals("Europe/Paris", pool.zoneId("not_found", ZoneId.of("Europe/Paris")).getId());
    }
}
