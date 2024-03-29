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
import fr.quatrevieux.araknemu.game.player.experience.PlayerExperienceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AddXpTest extends CommandTestCase {
    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        command = new AddXp(gamePlayer(true), container.get(PlayerExperienceService.class));
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
    void executeWithLevel() throws ContainerException, SQLException, AdminException {
        execute("addxp", "--level", "69");

        assertOutput("Add 15573541 xp to Bob (level = 69)");

        assertEquals(21055000, gamePlayer().properties().experience().current());
    }

    @Test
    void executeWithLevelAlias() throws ContainerException, SQLException, AdminException {
        execute("addxp", "-l", "69");

        assertOutput("Add 15573541 xp to Bob (level = 69)");

        assertEquals(21055000, gamePlayer().properties().experience().current());
    }

    @Test
    void executeWithLevelToLow() throws ContainerException, SQLException, AdminException {
        execute("addxp", "--level", "20");

        assertOutput("The player level (50) is already higher than the target level (20)");

        assertEquals(50, gamePlayer().properties().experience().level());
    }

    @Test
    void executeWithLevelSame() throws ContainerException, SQLException, AdminException {
        execute("addxp", "--level", "50");

        assertOutput("The player level (50) is already higher than the target level (50)");

        assertEquals(50, gamePlayer().properties().experience().level());
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
                "\taddxp [QUANTITY] [--level (-l) N]",
            "OPTIONS",
                "\tQUANTITY : The experience quantity to add. Must be an unsigned number.",
                "\t--level (-l) : The target level. If set, the quantity will be calculated to reach this level. Must be a positive number.",
            "EXAMPLES",
                "\t@John addxp 1000000     - Add 1 million xp to John",
                "\t@John addxp --level 150 - Add xp to John to reach level 150",
            "PERMISSIONS",
                "\t[ACCESS, MANAGE_PLAYER]"
        );
    }
}
