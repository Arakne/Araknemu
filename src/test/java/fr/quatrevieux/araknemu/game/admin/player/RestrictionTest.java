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
import fr.quatrevieux.araknemu.game.admin.exception.CommandException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class RestrictionTest extends CommandTestCase {
    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        command = new Restriction(gamePlayer());
    }

    @Test
    void executeBadOperation() throws ContainerException, SQLException, AdminException {
        assertThrows(CommandException.class, () -> execute("restriction", "*deny_chat"));
    }

    @Test
    void executeAddOneRestriction() throws ContainerException, SQLException, AdminException {
        execute("restriction", "+deny_chat");

        assertFalse(gamePlayer().restrictions().canChat());
        assertOutput("Bob restrictions updated");
    }

    @Test
    void executeRemoveOneRestriction() throws ContainerException, SQLException, AdminException {
        execute("restriction", "-allow_move_all_direction");

        assertFalse(gamePlayer().restrictions().canMoveAllDirections());
        assertOutput("Bob restrictions updated");
    }

    @Test
    void executeMultiple() throws ContainerException, SQLException, AdminException {
        execute("restriction", "-allow_move_all_direction", "+deny_chat", "+deny_challenge");

        assertFalse(gamePlayer().restrictions().canMoveAllDirections());
        assertFalse(gamePlayer().restrictions().canChat());
        assertFalse(gamePlayer().restrictions().canChallenge());

        assertOutput("Bob restrictions updated");
    }
}
