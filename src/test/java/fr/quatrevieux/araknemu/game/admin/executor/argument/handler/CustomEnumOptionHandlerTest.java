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
import org.kohsuke.args4j.Option;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CustomEnumOptionHandlerTest extends TestCase {
    static class Arguments {
        @Argument(handler = CustomEnumOptionHandler.class, metaVar = "value")
        public Values value;

        @Option(name = "--opt", handler = CustomEnumOptionHandler.class)
        public Values opt;

        @Option(name = "--le", handler = CustomEnumOptionHandler.class)
        private LongEnum le;

        enum Values {
            FOO,
            BAR,
            WITH_DASH,
        }

        enum LongEnum {
            A, B, C, D, E, F;
        }
    }

    static class Command extends AbstractCommand<Arguments> {
        @Override
        protected void build(AbstractCommand<Arguments>.Builder builder) {}

        @Override
        public String name() {
            return "foo";
        }

        @Override
        public void execute(AdminPerformer performer, Arguments arguments) throws AdminException {}

        @Override
        public Arguments createArguments() {
            return new Arguments();
        }
    }

    @Test
    void hydrate() throws Exception {
        Command command = new Command();
        AnnotationHydrator hydrator = new AnnotationHydrator();

        assertEquals(Arguments.Values.BAR, hydrator.hydrate(command, command.createArguments(), new CommandParser.Arguments("", "", "", Arrays.asList("foo", "bar"), null)).value);
        assertEquals(Arguments.Values.FOO, hydrator.hydrate(command, command.createArguments(), new CommandParser.Arguments("", "", "", Arrays.asList("foo", "FOO"), null)).value);
        assertEquals(Arguments.Values.WITH_DASH, hydrator.hydrate(command, command.createArguments(), new CommandParser.Arguments("", "", "", Arrays.asList("foo", "With-Dash"), null)).value);

        assertThrowsWithMessage(CmdLineException.class, "Invalid value invalid for argument \"value\". Available values : [foo, bar, with-dash]", () -> hydrator.hydrate(command, command.createArguments(), new CommandParser.Arguments("", "", "", Arrays.asList("foo", "invalid"), null)));
        assertThrowsWithMessage(CmdLineException.class, "Invalid value invalid for option \"--opt\". Available values : [foo, bar, with-dash]", () -> hydrator.hydrate(command, command.createArguments(), new CommandParser.Arguments("", "", "", Arrays.asList("foo", "--opt", "invalid"), null)));
    }
}
