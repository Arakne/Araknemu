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

package fr.quatrevieux.araknemu.realm;

import de.mkammerer.argon2.Argon2Factory;
import fr.quatrevieux.araknemu.core.config.IniDriver;
import org.ini4j.Ini;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class RealmConfigurationTest {
    private RealmConfiguration configuration;

    @BeforeEach
    void setUp() throws IOException {
        configuration = new RealmConfiguration();
        configuration.setPool(
            new IniDriver(
                new Ini(new File("src/test/test_config.ini"))
            ).pool("realm")
        );
    }

    @Test
    void port() {
        assertEquals(456, configuration.port());
    }

    @Test
    void clientVersion() {
        assertEquals("1.29.1", configuration.clientVersion());
    }

    @Test
    void inactivityTime() {
        assertEquals(Duration.ofMinutes(15), configuration.inactivityTime());
    }

    @Test
    void packetRateLimit() {
        assertEquals(100, configuration.packetRateLimit());
    }

    @Test
    void passwordHashAlgorithms() {
        assertArrayEquals(new String[] {"argon2", "plain"}, configuration.passwordHashAlgorithms());
    }

    @Test
    void banIpRefresh() {
        assertEquals(Duration.ofSeconds(30), configuration.banIpRefresh());
    }

    @Test
    void argon2() {
        assertEquals(4, configuration.argon2().iterations());
        assertEquals(8, configuration.argon2().parallelism());
        assertEquals(64*1024, configuration.argon2().memory());
        assertEquals(Argon2Factory.Argon2Types.ARGON2id, configuration.argon2().type());
    }
}
