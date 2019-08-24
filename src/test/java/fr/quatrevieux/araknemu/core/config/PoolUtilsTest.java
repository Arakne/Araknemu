package fr.quatrevieux.araknemu.core.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PoolUtilsTest {
    private PoolUtils pool;

    @BeforeEach
    void setUp() {
        pool = new PoolUtils(
            new Pool() {
                final private Map<String, String> map = new HashMap<>();

                {
                    map.put("test_bool", "yes");
                    map.put("test_int", "774");
                    map.put("test_string", "foo");
                    map.put("test_double", "1.5");
                }

                @Override
                public boolean has(String key) {
                    return map.containsKey(key);
                }

                @Override
                public String get(String key) {
                    return map.get(key);
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
}
