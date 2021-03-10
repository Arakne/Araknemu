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
 * Copyright (c) 2017-2021 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.core.config;

import fr.quatrevieux.araknemu._test.TestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class IniLoaderTest extends TestCase {
    IniLoader loader;

    @BeforeEach
    void setUp() {
        loader = new IniLoader();
    }

    @Test
    void supports() {
        assertFalse(loader.supports(Paths.get("test.txt")));
        assertFalse(loader.supports(Paths.get("test")));
        assertFalse(loader.supports(Paths.get("test.txt").toAbsolutePath()));

        assertTrue(loader.supports(Paths.get("test.ini")));
        assertTrue(loader.supports(Paths.get("test.ini").toAbsolutePath()));
        assertTrue(loader.supports(Paths.get("test.ini.dist")));
        assertTrue(loader.supports(Paths.get("test.ini.dist").toAbsolutePath()));
    }

    @Test
    void load() throws IOException {
        Driver driver = loader.load(Paths.get("src/test/test_config.ini"));

        assertInstanceOf(IniDriver.class, driver);

        assertTrue(driver.has("foo"));
        assertEquals("baz", driver.pool("foo").get("bar"));
    }
}
