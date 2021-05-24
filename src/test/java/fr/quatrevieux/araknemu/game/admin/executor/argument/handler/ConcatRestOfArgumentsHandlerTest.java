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

package fr.quatrevieux.araknemu.game.admin.executor.argument.handler;

import fr.quatrevieux.araknemu._test.TestCase;
import fr.quatrevieux.araknemu.game.admin.AbstractCommand;
import fr.quatrevieux.araknemu.game.admin.AdminPerformer;
import fr.quatrevieux.araknemu.game.admin.CommandParser;
import fr.quatrevieux.araknemu.game.admin.exception.AdminException;
import fr.quatrevieux.araknemu.game.admin.executor.argument.AnnotationHydrator;
import org.junit.jupiter.api.Test;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;

import java.time.Duration;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class ConcatRestOfArgumentsHandlerTest extends TestCase {
    static class Arguments {
        @Argument(index = 0)
        public String foo;

        @Argument(index = 1, handler = ConcatRestOfArgumentsHandler.class)
        public String args;
    }

    static class Command extends AbstractCommand<ConcatRestOfArgumentsHandlerTest.Arguments> {
        @Override
        protected void build(AbstractCommand<ConcatRestOfArgumentsHandlerTest.Arguments>.Builder builder) {}

        @Override
        public String name() {
            return "foo";
        }

        @Override
        public void execute(AdminPerformer performer, ConcatRestOfArgumentsHandlerTest.Arguments arguments) throws AdminException {}

        @Override
        public ConcatRestOfArgumentsHandlerTest.Arguments createArguments() {
            return new ConcatRestOfArgumentsHandlerTest.Arguments();
        }
    }

    @Test
    void hydrate() throws Exception {
        ConcatRestOfArgumentsHandlerTest.Command command = new ConcatRestOfArgumentsHandlerTest.Command();
        AnnotationHydrator hydrator = new AnnotationHydrator();

        assertNull(hydrator.hydrate(command, command.createArguments(), new CommandParser.Arguments("", "", "", Arrays.asList("foo", "bar"), null)).args);
        assertEquals("baz", hydrator.hydrate(command, command.createArguments(), new CommandParser.Arguments("", "", "", Arrays.asList("foo", "bar", "baz"), null)).args);
        assertEquals("baz oof", hydrator.hydrate(command, command.createArguments(), new CommandParser.Arguments("", "", "", Arrays.asList("foo", "bar", "baz", "oof"), null)).args);
    }
}