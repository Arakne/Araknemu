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

package fr.quatrevieux.araknemu.game.admin.executor.argument;

import fr.quatrevieux.araknemu.common.account.Permission;
import fr.quatrevieux.araknemu.game.admin.AdminPerformer;
import fr.quatrevieux.araknemu.game.admin.Command;
import fr.quatrevieux.araknemu.game.admin.CommandParser;
import fr.quatrevieux.araknemu.game.admin.exception.AdminException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VoidHydratorTest {
    private VoidHydrator hydrator;

    @BeforeEach
    void setUp() {
        hydrator = new VoidHydrator();
    }

    @Test
    void notSupported() {
        Command<Object> command = new Command<Object>() {
            @Override
            public String name() {
                return "foo";
            }

            @Override
            public String description() {
                return null;
            }

            @Override
            public String help() {
                return null;
            }

            @Override
            public void execute(AdminPerformer performer, Object arguments) throws AdminException {

            }

            @Override
            public Set<Permission> permissions() {
                return null;
            }
        };

        assertFalse(hydrator.supports(command, null));
        assertFalse(hydrator.supports(command, "foo"));
        assertFalse(hydrator.supports(command, new Object()));
    }

    @Test
    void implicit() throws Exception {
        Command<Void> command = new Command<Void>() {
            @Override
            public String name() {
                return "foo";
            }

            @Override
            public String description() {
                return null;
            }

            @Override
            public String help() {
                return null;
            }

            @Override
            public void execute(AdminPerformer performer, Void arguments) throws AdminException {

            }

            @Override
            public Set<Permission> permissions() {
                return null;
            }
        };

        assertTrue(hydrator.supports(command, null));
        assertNull(hydrator.hydrate(command, null, new CommandParser.Arguments("", "", "", Arrays.asList("foo", "bar"), null)));
    }
}
