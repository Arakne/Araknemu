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
import fr.quatrevieux.araknemu.core.dbal.DatabaseConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class ConfigurationLoaderTest extends TestCase {
    private ConfigurationLoader loader;
    private Path basePath;

    @BeforeEach
    void setUp() {
        loader = new ConfigurationLoader(basePath = Paths.get("src/test"));
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(basePath.resolve("config.ini"));
        Files.deleteIfExists(basePath.resolve("config.ini.dist"));
    }

    @Test
    void loadDefaultConfig() throws IOException {
        Files.copy(basePath.resolve("test_config.ini"), basePath.resolve("config.ini"));

        Configuration config = loader.load();
        DatabaseConfiguration dbConfig = config.module(DatabaseConfiguration.MODULE);

        assertEquals("sqlite", dbConfig.connection("realm").type());
        assertEquals("localhost", dbConfig.connection("test_mysql").host());
        assertEquals("araknemu", dbConfig.connection("test_mysql").user());
    }

    @Test
    void loadDefaultDistConfig() throws IOException {
        Files.copy(basePath.resolve("test_config.ini"), basePath.resolve("config.ini.dist"));

        Configuration config = loader.load();
        DatabaseConfiguration dbConfig = config.module(DatabaseConfiguration.MODULE);

        assertEquals("sqlite", dbConfig.connection("realm").type());
        assertEquals("localhost", dbConfig.connection("test_mysql").host());
        assertEquals("araknemu", dbConfig.connection("test_mysql").user());
    }

    @Test
    void loadWithConfigurationFileName() throws IOException {
        Configuration config = loader.configFileName("test_config.ini").load();
        DatabaseConfiguration dbConfig = config.module(DatabaseConfiguration.MODULE);

        assertEquals("sqlite", dbConfig.connection("realm").type());
        assertEquals("localhost", dbConfig.connection("test_mysql").host());
        assertEquals("araknemu", dbConfig.connection("test_mysql").user());
    }

    @Test
    void loadWithConfigurationFilePath() throws IOException {
        Configuration config = loader.configFile(basePath.resolve("test_config.ini").toAbsolutePath()).load();
        DatabaseConfiguration dbConfig = config.module(DatabaseConfiguration.MODULE);

        assertEquals("sqlite", dbConfig.connection("realm").type());
        assertEquals("localhost", dbConfig.connection("test_mysql").host());
        assertEquals("araknemu", dbConfig.connection("test_mysql").user());
    }

    @Test
    void loadWithConfigurationFileNotFoundOrNotHandled() {
        assertThrows(IllegalArgumentException.class, () -> loader.load());
        assertThrows(IllegalArgumentException.class, () -> loader.configFileName("not_found").load());
        assertThrows(IllegalArgumentException.class, () -> loader.configFileName("java/fr/quatrevieux/araknemu/_test/TestCase.java").load());
    }
}
