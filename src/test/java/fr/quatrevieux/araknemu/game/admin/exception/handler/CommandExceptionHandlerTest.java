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

package fr.quatrevieux.araknemu.game.admin.exception.handler;

import fr.quatrevieux.araknemu.game.admin.CommandParser;
import fr.quatrevieux.araknemu.game.admin.CommandTestCase;
import fr.quatrevieux.araknemu.game.admin.exception.AdminException;
import fr.quatrevieux.araknemu.game.admin.exception.CommandException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;

class CommandExceptionHandlerTest extends CommandTestCase {
    private CommandExceptionHandler handler;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        handler = new CommandExceptionHandler();
        performer = new PerformerWrapper(user());
    }

    @Test
    void withoutArguments() throws AdminException, SQLException {
        CommandException exception = new CommandException("foo", "bar");

        handler.handle(performer, exception, null);

        assertOutput(
            "An error occurs during execution of 'foo' : bar",
            "See <u><a href='asfunction:onHref,ExecCmd, help foo,true'>help</a></u> for usage"
        );
    }

    @Test
    void withArgumentsAndContext() throws AdminException, SQLException {
        CommandException exception = new CommandException("foo", "bar");
        CommandParser.Arguments arguments = new CommandParser.Arguments("", "@John", "foo", new ArrayList<>(), null);

        handler.handle(performer, exception, arguments);

        assertOutput(
            "An error occurs during execution of 'foo' : bar",
            "See <u><a href='asfunction:onHref,ExecCmd,@John help foo,true'>help</a></u> for usage"
        );
    }
}
