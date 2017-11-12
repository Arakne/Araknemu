package fr.quatrevieux.araknemu.core.config;

import org.ini4j.Ini;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

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
}
