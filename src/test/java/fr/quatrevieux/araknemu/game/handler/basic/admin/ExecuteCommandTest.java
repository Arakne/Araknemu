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

package fr.quatrevieux.araknemu.game.handler.basic.admin;

import fr.quatrevieux.araknemu.common.account.Permission;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.admin.AdminSessionService;
import fr.quatrevieux.araknemu.game.admin.LogType;
import fr.quatrevieux.araknemu.network.game.in.basic.admin.AdminCommand;
import fr.quatrevieux.araknemu.network.game.out.basic.admin.CommandResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ExecuteCommandTest extends GameBaseCase {
    private ExecuteCommand handler;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        gamePlayer();
        gamePlayer().account().grant(Permission.ACCESS);

        handler = new ExecuteCommand(
            container.get(AdminSessionService.class)
        );
    }

    @Test
    void handleSuccess() throws Exception {
        handler.handle(session, new AdminCommand("echo hello world"));

        requestStack.assertLast(
            new CommandResult(LogType.DEFAULT, "hello world")
        );
    }

    @Test
    void handleWithError() throws Exception {
        handler.handle(session, new AdminCommand("badCommand"));

        requestStack.assertLast(
            new CommandResult(LogType.ERROR, "Command 'badCommand' is not found")
        );
    }
}
