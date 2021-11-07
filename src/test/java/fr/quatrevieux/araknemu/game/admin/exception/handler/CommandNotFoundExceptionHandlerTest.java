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
import fr.quatrevieux.araknemu.game.admin.exception.CommandNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class CommandNotFoundExceptionHandlerTest extends CommandTestCase {
    private CommandNotFoundExceptionHandler handler;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        handler = new CommandNotFoundExceptionHandler();
        performer = new PerformerWrapper(user());
    }

    @Test
    void withoutArguments() {
        CommandNotFoundException exception = new CommandNotFoundException("foo");

        handler.handle(performer, exception, null);

        assertOutput("Command 'foo' is not found");
    }

    @Test
    void withArgumentsShouldFoundNearestCommandName() {
        CommandNotFoundException exception = new CommandNotFoundException("gitu");
        CommandParser.Arguments arguments = new CommandParser.Arguments("", "!", "gitu", Arrays.asList("gitu", "map", "10340"), performer.self());

        handler.handle(performer, exception, arguments);

        assertOutput(
            "Command 'gitu' is not found",
            "Did you mean <u><a href='asfunction:onHref,ExecCmd,! goto map 10340,false'>goto</a></u> ? (See <u><a href='asfunction:onHref,ExecCmd,! help goto,true'>help</a></u>)"
        );
    }
}
