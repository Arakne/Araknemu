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

import fr.quatrevieux.araknemu.core.config.IniDriver;
import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvBuilder;
import org.ini4j.Ini;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class EnvDriverTest {
    private IniDriver innerDriver;
    private EnvDriver driver;
    private Dotenv dotenv;

    @BeforeEach
    void setUp() throws IOException {
        dotenv = new DotenvBuilder()
            .directory("src/test")
            .filename("unit_dotenv")
            .load()
        ;

        innerDriver = new IniDriver(new Ini(new File("src/test/test_config_env.ini")));
        driver = new EnvDriver(innerDriver, dotenv);
    }

    @Test
    void has() {
        assertTrue(driver.has("test"));
        assertFalse(driver.has("not_found"));
    }

    @Test
    void get() {
        assertSame(innerDriver.get("test"), driver.get("test"));
        assertSame(innerDriver.get("not_found"), driver.get("not_found"));
    }

    @Test
    void pool() {
        assertInstanceOf(EnvPool.class, driver.pool("test"));
        assertInstanceOf(EnvPool.class, driver.pool("not_found"));

        assertEquals("Hello Robert!", driver.pool("test").get("message"));
        assertEquals("just a test string", driver.pool("test").get("foo"));
        assertEquals("test ???", driver.pool("test").get("with_default"));
        assertEquals("raw string", driver.pool("test").get("raw"));
    }
}
