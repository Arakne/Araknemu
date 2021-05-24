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

package fr.quatrevieux.araknemu.game.admin.executor.argument;

import fr.quatrevieux.araknemu._test.TestCase;
import fr.quatrevieux.araknemu.game.admin.AbstractCommand;
import fr.quatrevieux.araknemu.game.admin.AdminPerformer;
import fr.quatrevieux.araknemu.game.admin.CommandParser;
import fr.quatrevieux.araknemu.game.admin.exception.AdminException;
import fr.quatrevieux.araknemu.game.admin.exception.CommandException;
import fr.quatrevieux.araknemu.game.admin.help.CommandHelp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kohsuke.args4j.Argument;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HydratorsAggregateTest extends TestCase {
    private HydratorsAggregate hydrator;

    @BeforeEach
    void setUp() {
        hydrator = new HydratorsAggregate();
    }

    @Test
    void supports() {
        assertTrue(hydrator.supports(new CommandWithString(), null));
        assertFalse(hydrator.supports(new CommandWithObject(), null));
    }

    @Test
    void hydrate() throws Exception {
        CommandParser.Arguments parsedArguments = new CommandParser.Arguments("", "", "", Arrays.asList("foo", "bar"), null);

        assertEquals("bar", hydrator.hydrate(new CommandWithString(), null, parsedArguments));
        assertIterableEquals(Collections.singletonList("bar"), hydrator.hydrate(new CommandWithStringList(), null, parsedArguments));
        assertNull(hydrator.hydrate(new CommandWithVoid(), null, parsedArguments));
        assertEquals("bar", hydrator.hydrate(new CommandWithObject(), new CommandWithObject().createArguments(), parsedArguments).bar);
    }

    @Test
    void hydrateNotSupported() throws Exception {
        CommandParser.Arguments parsedArguments = new CommandParser.Arguments("", "", "", Arrays.asList("foo", "bar"), null);

        assertThrowsWithMessage(CommandException.class, "Cannot parse arguments for command CommandWithObject", () -> hydrator.hydrate(new CommandWithObject(), null, parsedArguments));
    }

    @Test
    void helpNotSupported() {
        CommandWithObject command = new CommandWithObject();
        CommandHelp help = command.help();

        assertSame(help, hydrator.help(command, null, help));

        assertEquals(
            "foo - No description\n" +
            "========================================\n" +
            "\n" +
            "<b>SYNOPSIS</b>\n" +
                "\tfoo\n" +
            "\n" +
            "<b>PERMISSIONS</b>\n" +
                "\t[ACCESS]",
            help.toString()
        );
    }

    @Test
    void help() {
        CommandWithObject command = new CommandWithObject();
        CommandHelp help = command.help();

        CommandHelp newHelp = hydrator.help(command, command.createArguments(), help);
        assertNotSame(help, newHelp);
        assertNotEquals(help.toString(), newHelp.toString());

        assertEquals(
            "foo - No description\n" +
            "========================================\n" +
            "\n" +
            "<b>SYNOPSIS</b>\n" +
                "\tfoo [BAR=_]\n" +
            "\n" +
                "<b>OPTIONS</b>\n" +
                    "\tBAR : Define bar\n" +
            "\n" +
            "<b>PERMISSIONS</b>\n" +
                "\t[ACCESS]",
            newHelp.toString()
        );
    }

    static class CommandWithString extends AbstractCommand<String> {
        @Override
        protected void build(AbstractCommand<String>.Builder builder) {}

        @Override
        public String name() {
            return "foo";
        }

        @Override
        public void execute(AdminPerformer performer, String arguments) throws AdminException {}
    }

    static class CommandWithStringList extends AbstractCommand<List<String>> {
        @Override
        protected void build(AbstractCommand<List<String>>.Builder builder) {}

        @Override
        public String name() {
            return "foo";
        }

        @Override
        public void execute(AdminPerformer performer, List<String> arguments) throws AdminException {}
    }

    static class CommandWithVoid extends AbstractCommand<Void> {
        @Override
        protected void build(AbstractCommand<Void>.Builder builder) {}

        @Override
        public String name() {
            return "foo";
        }

        @Override
        public void execute(AdminPerformer performer, Void arguments) throws AdminException {}
    }

    static class CommandWithObject extends AbstractCommand<CommandWithObject.Arguments> {
        @Override
        protected void build(AbstractCommand<Arguments>.Builder builder) {}

        @Override
        public String name() {
            return "foo";
        }

        @Override
        public void execute(AdminPerformer performer, Arguments arguments) throws AdminException {}

        static public class Arguments {
            @Argument(metaVar = "BAR", usage = "Define bar")
            public String bar = "_";
        }

        @Override
        public Arguments createArguments() {
            return new Arguments();
        }
    }
}
