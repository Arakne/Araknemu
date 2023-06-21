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
import fr.quatrevieux.araknemu.game.admin.CommandParser;
import fr.quatrevieux.araknemu.game.admin.CommandTestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.EnumSet;

class ExceptionHandlerTest extends CommandTestCase {
    private ExceptionHandler handler;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        performer = new PerformerWrapper(user());
        handler = new ExceptionHandler();
    }

    @Test
    void simpleErrors() {
        assertHandle(new CommandNotFoundException("TEST"), "Command 'TEST' is not found");
        assertHandle(new CommandException("TEST", "MY ERROR"),
            "An error occurs during execution of 'TEST' : MY ERROR",
            "See <u><a href='asfunction:onHref,ExecCmd, help TEST,true'>help</a></u> for usage"
        );
        assertHandle(new CommandPermissionsException("TEST", EnumSet.of(Permission.ACCESS, Permission.MANAGE_PLAYER)), "Unauthorized command 'TEST', you need at least these permissions [ACCESS, MANAGE_PLAYER]");
        assertHandle(new ContextException("MY ERROR"), "Error during resolving context : MY ERROR");
        assertHandle(new ContextException(), "Error during resolving context : fr.quatrevieux.araknemu.game.admin.exception.ContextException");
        assertHandle(new ContextNotFoundException("TEST"), "The context 'TEST' is not found");
        assertHandle(new Exception("my error"), "Error : java.lang.Exception: my error");
    }

    @Test
    void commandExecutionError() {
        CommandParser.Arguments arguments = new CommandParser.Arguments("", "!", "foo", Arrays.asList("foo", "bar", "baz"), performer.self());

        assertHandle(new CommandExecutionException(arguments, new CommandNotFoundException("foo")),
            "Command 'foo' is not found",
            "Did you mean <u><a href='asfunction:onHref,ExecCmd,! goto bar baz,false'>goto</a></u> ? (See <u><a href='asfunction:onHref,ExecCmd,! help goto,true'>help</a></u>)"
        );
        assertHandle(new CommandExecutionException(arguments, new CommandException("foo", "MY ERROR")),
            "An error occurs during execution of 'foo' : MY ERROR",
            "See <u><a href='asfunction:onHref,ExecCmd,! help foo,true'>help</a></u> for usage"
        );
        assertHandle(new CommandExecutionException(arguments, new CommandPermissionsException("foo", EnumSet.of(Permission.ACCESS, Permission.MANAGE_PLAYER))), "Unauthorized command 'foo', you need at least these permissions [ACCESS, MANAGE_PLAYER]");
        assertHandle(new CommandExecutionException(arguments, new ContextException("MY ERROR")), "Error during resolving context : MY ERROR");
        assertHandle(new CommandExecutionException(arguments, new ContextNotFoundException("foo")), "The context 'foo' is not found");
        assertHandle(new CommandExecutionException(arguments, new Exception("my error")),
            "An error occurs during execution of 'foo' : my error",
            "See <u><a href='asfunction:onHref,ExecCmd,! help foo,true'>help</a></u> for usage"
        );
    }

    public void assertHandle(Exception e, String... messages) {
        handler.handle(performer, e);
        assertOutput(messages);
        performer.logs.clear();
    }
}