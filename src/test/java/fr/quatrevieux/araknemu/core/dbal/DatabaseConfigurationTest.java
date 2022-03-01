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

package fr.quatrevieux.araknemu.core.dbal;

import fr.quatrevieux.araknemu.core.config.DefaultConfiguration;
import fr.quatrevieux.araknemu.core.config.IniDriver;
import org.ini4j.Ini;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseConfigurationTest {
    private DatabaseConfiguration configuration;

    @BeforeEach
    void setUp() throws IOException {
        configuration = new DefaultConfiguration(new IniDriver(new Ini(new File("src/test/test_config.ini"))))
                .module(DatabaseConfiguration.MODULE)
        ;
    }

    @Test
    void module() {
        assertNotNull(configuration.connection("realm"));
        assertEquals("realm", configuration.connection("realm").name());
        assertNotSame(configuration.connection("realm"), configuration.connection("test_sqlite"));
    }

    @Test
    void realm() {
        DatabaseConfiguration.Connection config = configuration.connection("realm");

        assertEquals("sqlite", config.type());
        assertTrue(config.memory());
        assertTrue(config.shared());
        assertEquals("realm", config.dbname());
        assertEquals("realm.db", config.path());
        assertEquals(4, config.maxPoolSize());
    }

    @Test
    void test_sqlite() {
        DatabaseConfiguration.Connection config = configuration.connection("test_sqlite");

        assertEquals("sqlite", config.type());
        assertFalse(config.memory());
        assertEquals("test_sqlite", config.dbname());
        assertEquals("test.db", config.path());
        assertEquals(16, config.maxPoolSize());
    }

    @Test
    void test_mysql() {
        DatabaseConfiguration.Connection config = configuration.connection("test_mysql");

        assertEquals("mysql", config.type());
        assertEquals("araknemu", config.dbname());
        assertEquals("araknemu", config.user());
        assertEquals("", config.password());
        assertEquals(16, config.maxPoolSize());
    }

    @Test
    void defaults() {
        DatabaseConfiguration.Connection config = configuration.connection("test_sqlite");

        assertTrue(config.autoReconnect());
        assertEquals(3600, config.refreshPoolInterval());
    }
}