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

package fr.quatrevieux.araknemu.game.admin.exception;

import fr.quatrevieux.araknemu.common.account.Permission;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.admin.AdminSessionService;
import fr.quatrevieux.araknemu.game.admin.AdminUser;
import fr.quatrevieux.araknemu.game.admin.LogType;
import fr.quatrevieux.araknemu.network.game.out.basic.admin.CommandResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;

class ExceptionHandlerTest extends GameBaseCase {
    private ExceptionHandler handler;
    private AdminUser adminUser;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        adminUser = container.get(AdminSessionService.class).user(gamePlayer());
        handler = new ExceptionHandler();
    }

    @Test
    void errors() {
        assertHandle("Command 'TEST' is not found", new CommandNotFoundException("TEST"));
        assertHandle("An error occurs during execution of 'TEST' : MY ERROR", new CommandException("TEST", "MY ERROR"));
        assertHandle("Unauthorized command 'TEST', you need at least these permissions [ACCESS, MANAGE_PLAYER]", new CommandPermissionsException("TEST", EnumSet.of(Permission.ACCESS, Permission.MANAGE_PLAYER)));
        assertHandle("Error during resolving context : MY ERROR", new ContextException("MY ERROR"));
        assertHandle("The context 'TEST' is not found", new ContextNotFoundException("TEST"));
        assertHandle("Error : java.lang.Exception: my error", new Exception("my error"));
    }

    public void assertMessage(String message) {
        requestStack.assertLast(
            new CommandResult(LogType.ERROR, message)
        );
    }

    public void assertHandle(String message, Exception e) {
        handler.handle(adminUser, e);
        assertMessage(message);
    }
}