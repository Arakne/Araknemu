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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DefaultConfigurationTest {
    static public class Foo {
        public static final ConfigurationModule<Foo> MODULE = new ConfigurationModule<Foo>() {
            @Override
            public Foo create(Pool pool) {
                return new Foo(pool);
            }

            @Override
            public String name() {
                return "foo";
            }
        };

        public final Pool pool;

        public Foo(Pool pool) {
            this.pool = pool;
        }

    }

    static public class Empty {
        public static final ConfigurationModule<Empty> MODULE = new ConfigurationModule<Empty>() {
            @Override
            public Empty create(Pool pool) {
                return new Empty(pool);
            }

            @Override
            public String name() {
                return "empty";
            }
        };

        public final Pool pool;

        public Empty(Pool pool) {
            this.pool = pool;
        }
    }

    private DefaultConfiguration configuration;

    @BeforeEach
    void setUp() throws IOException {
        configuration = new DefaultConfiguration(
            new IniDriver(
                new Ini(new File("src/test/test_config.ini"))
            )
        );
    }

    @Test
    void moduleOnFirstCall() {
        Foo module = configuration.module(Foo.MODULE);

        assertTrue(module.pool instanceof IniPool);
        assertEquals("baz", module.pool.get("bar"));
    }

    @Test
    void moduleSameInstance() {
        assertSame(configuration.module(Foo.MODULE), configuration.module(Foo.MODULE));
    }

    @Test
    void moduleNotFound() {
        Empty module = configuration.module(Empty.MODULE);

        assertTrue(module.pool instanceof EmptyPool);
    }
}
