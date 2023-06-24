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

package fr.quatrevieux.araknemu.game.admin.global;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.admin.CommandTestCase;
import fr.quatrevieux.araknemu.game.admin.exception.AdminException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

class EchoTest extends CommandTestCase {
    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        command = new Echo();
    }

    @Test
    void executeSimple() throws ContainerException, SQLException, AdminException {
        execute("echo", "Hello", "World", "!");

        assertInfo("Hello World !");
    }

    @Test
    void executeInfo() throws ContainerException, SQLException, AdminException {
        execute("echo", "-i", "Hello", "World", "!");

        assertInfo("Hello World !");
    }

    @Test
    void executeSuccess() throws ContainerException, SQLException, AdminException {
        execute("echo", "-s", "Hello", "World", "!");

        assertSuccess("Hello World !");
    }

    @Test
    void executeError() throws ContainerException, SQLException, AdminException {
        execute("echo", "-e", "Hello", "World", "!");

        assertError("Hello World !");
    }
}