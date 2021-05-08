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

import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LocalTimeHandlerTest extends TestCase {
    static class Arguments {
        @Argument
        public LocalTime time;
    }

    static class Command extends AbstractCommand<LocalTimeHandlerTest.Arguments> {
        @Override
        protected void build(AbstractCommand<LocalTimeHandlerTest.Arguments>.Builder builder) {}

        @Override
        public String name() {
            return "foo";
        }

        @Override
        public void execute(AdminPerformer performer, LocalTimeHandlerTest.Arguments arguments) throws AdminException {}

        @Override
        public LocalTimeHandlerTest.Arguments createArguments() {
            return new LocalTimeHandlerTest.Arguments();
        }
    }

    @Test
    void hydrate() throws Exception {
        LocalTimeHandlerTest.Command command = new LocalTimeHandlerTest.Command();
        AnnotationHydrator hydrator = new AnnotationHydrator();

        assertEquals(LocalTime.of(15, 22, 0), hydrator.hydrate(command, command.createArguments(), new CommandParser.Arguments("", "", "", Arrays.asList("foo", "15:22"), null)).time);
        assertEquals(LocalTime.of(15, 22, 36), hydrator.hydrate(command, command.createArguments(), new CommandParser.Arguments("", "", "", Arrays.asList("foo", "15:22:36"), null)).time);

        assertThrows(DateTimeParseException.class, () -> hydrator.hydrate(command, command.createArguments(), new CommandParser.Arguments("", "", "", Arrays.asList("foo", "invalid"), null)));
    }
}
