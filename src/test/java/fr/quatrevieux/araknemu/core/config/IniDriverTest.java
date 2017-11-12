package fr.quatrevieux.araknemu.core.config;

import org.ini4j.Ini;
import org.ini4j.Profile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class IniDriverTest {
    private IniDriver driver;

    @BeforeEach
    void setUp() throws IOException {
        driver = new IniDriver(
            new Ini(new File("src/test/test_config.ini"))
        );
    }

    @Test
    void has() {
        assertTrue(driver.has("realm"));
        assertFalse(driver.has("not_found"));
    }

    @Test
    void get() {
        assertTrue(driver.get("realm") instanceof Profile.Section);
    }

    @Test
    void pool() {
        Pool pool = driver.pool("realm");

        assertTrue(pool instanceof IniPool);
        assertEquals("456", pool.get("server.port"));
    }
}
