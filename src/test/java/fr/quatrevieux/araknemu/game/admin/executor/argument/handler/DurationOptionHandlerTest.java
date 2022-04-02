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

class DurationOptionHandlerTest extends TestCase {
    static class Arguments {
        @Argument
        public Duration duration;
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

        assertEquals(Duration.ofSeconds(10), hydrator.hydrate(command, command.createArguments(), new CommandParser.Arguments("", "", "", Arrays.asList("foo", "10s"), null)).duration);
        assertEquals(Duration.ofSeconds(10), hydrator.hydrate(command, command.createArguments(), new CommandParser.Arguments("", "", "", Arrays.asList("foo", "pt10s"), null)).duration);
        assertEquals(Duration.ofSeconds(185), hydrator.hydrate(command, command.createArguments(), new CommandParser.Arguments("", "", "", Arrays.asList("foo", "3m5s"), null)).duration);
        assertEquals(Duration.parse("PT1H15M"), hydrator.hydrate(command, command.createArguments(), new CommandParser.Arguments("", "", "", Arrays.asList("foo", "1h15m"), null)).duration);
        assertEquals(Duration.parse("P1DT3H"), hydrator.hydrate(command, command.createArguments(), new CommandParser.Arguments("", "", "", Arrays.asList("foo", "1dt3h"), null)).duration);

        assertThrows(CmdLineException.class, () -> hydrator.hydrate(command, command.createArguments(), new CommandParser.Arguments("", "", "", Arrays.asList("foo", "invalid"), null)));
        assertThrows(CmdLineException.class, () -> hydrator.hydrate(command, command.createArguments(), new CommandParser.Arguments("", "", "", Arrays.asList("foo", ""), null)));
    }
}
