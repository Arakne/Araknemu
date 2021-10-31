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

package fr.quatrevieux.araknemu.game.admin.server;

import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.game.SavingService;
import fr.quatrevieux.araknemu.game.admin.CommandTestCase;
import fr.quatrevieux.araknemu.game.admin.exception.AdminException;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class SaveTest extends CommandTestCase {
    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        command = new Save(container.get(SavingService.class));
    }

    @Test
    void executeSuccess() throws SQLException, AdminException, InterruptedException {
        GamePlayer player = gamePlayer(true);
        player.setPosition(new Position(123, 45));

        execute("save");
        Thread.sleep(100);

        assertOutput("Start save");
        assertEquals(new Position(123, 45), dataSet.refresh(new Player(player.id())).position());
    }

    @Test
    void executeAlreadyInProgress() throws Exception {
        gamePlayer(true);
        makeOtherPlayer(1);

        execute("save");
        execute("save");
        assertOutput("A save is already in progress");
    }

    @Test
    void help() {
        assertHelp(
            "save - Save all connected players",
            "========================================",
            "SYNOPSIS",
                "\tsave",
            "PERMISSIONS",
                "\t[ACCESS, MANAGE_PLAYER]"
        );
    }
}
