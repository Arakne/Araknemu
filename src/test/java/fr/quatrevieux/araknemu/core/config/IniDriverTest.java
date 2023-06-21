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

import org.ini4j.Ini;
import org.ini4j.Profile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
