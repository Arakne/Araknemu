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
import fr.quatrevieux.araknemu.game.admin.exception.ContextException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class InfoTest extends CommandTestCase {
    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        command = new Info(gamePlayer());
    }

    @Test
    void execute() throws ContainerException, SQLException, AdminException {
        execute("info");

        assertOutput(
            "Player info : Bob",
            "==============================",
            "ID:    1",
            "Name:  Bob",
            "Level: 50",
            "Race:  Feca",
            "Sex:   MALE",
            "GfxID: 10",
            "=============================="
        );
    }

    @Test
    void help() {
        assertHelp(
            "info - Display information on the selected player",
            "========================================",
            "SYNOPSIS",
                "\t[context] info",
            "Note: this command takes no arguments, the account is only resolved by the context",
            "EXAMPLES",
                "\t${player:Alan} info - Display information about the player Alan",
                "\tinfo                - Display self player information",
            "PERMISSIONS",
                "\t[ACCESS, MANAGE_PLAYER]"
        );
    }
}
