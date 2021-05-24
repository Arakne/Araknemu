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

package fr.quatrevieux.araknemu.game.admin.player;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.admin.CommandTestCase;
import fr.quatrevieux.araknemu.game.admin.exception.AdminException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class AddXpTest extends CommandTestCase {
    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        command = new AddXp(gamePlayer(true));
    }

    @Test
    void executeWithoutLevelUp() throws ContainerException, SQLException, AdminException {
        execute("addxp", "1000");

        assertOutput("Add 1000 xp to Bob (level = 50)");

        assertEquals(5482459, gamePlayer().properties().experience().current());
    }

    @Test
    void executeWithLevelUp() throws ContainerException, SQLException, AdminException {
        execute("addxp", "1000000");

        assertOutput("Add 1000000 xp to Bob (level = 52)");

        assertEquals(6481459, gamePlayer().properties().experience().current());
    }

    @Test
    void executeWithLongNumber() throws ContainerException, SQLException, AdminException {
        execute("addxp", "10000000000");

        assertOutput("Add 10000000000 xp to Bob (level = 200)");

        assertEquals(10005481459L, gamePlayer().properties().experience().current());
    }

    @Test
    void help() {
        assertHelp(
            "addxp - Add experience to player",
            "========================================",
            "SYNOPSIS",
                "\taddxp QUANTITY",
            "OPTIONS",
                "\tQUANTITY : The experience quantity to add. Must be an unsigned number.",
            "EXAMPLES",
                "\t${player:John} addxp 1000000 - Add 1 million xp to John",
            "PERMISSIONS",
                "\t[ACCESS, MANAGE_PLAYER]"
        );
    }
}
