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

import fr.quatrevieux.araknemu.common.account.Permission;
import fr.quatrevieux.araknemu.game.admin.AdminPerformer;
import fr.quatrevieux.araknemu.game.admin.Command;
import fr.quatrevieux.araknemu.game.admin.CommandParser;
import fr.quatrevieux.araknemu.game.admin.exception.AdminException;
import fr.quatrevieux.araknemu.game.admin.exception.CommandException;
import fr.quatrevieux.araknemu.game.admin.formatter.HelpFormatter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.Option;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class AnnotationHydratorTest {
    private AnnotationHydrator hydrator;

    @BeforeEach
    void setUp() {
        hydrator = new AnnotationHydrator();
    }

    @Test
    void notSupported() {
        Command<Object> command = new Command<Object>() {
            @Override
            public String name() {
                return "foo";
            }

            @Override
            public String description() {
                return null;
            }

            @Override
            public HelpFormatter help() {
                return null;
            }

            @Override
            public void execute(AdminPerformer performer, Object arguments) throws AdminException {

            }

            @Override
            public Set<Permission> permissions() {
                return null;
            }
        };

        assertFalse(hydrator.supports(command, null));
    }

    static class Arguments {
        @Argument(required = true, metaVar = "FOO", usage = "My foo")
        public String foo;

        @Option(name = "--bar", usage = "Enable bar")
        public boolean bar = false;

        @Argument(index = 1, metaVar = "DEF", usage = "Arg with default")
        public int withDefault = 42;

        @Option(name = "--opt", usage = "Option with default")
        public String optWithDefault = "default";
    }

    @Test
    void explicitCreateArgumentType() throws Exception {
        Command<Arguments> command = new Command<Arguments>() {
            @Override
            public String name() {
                return "foo";
            }

            @Override
            public String description() {
                return null;
            }

            @Override
            public HelpFormatter help() {
                return null;
            }

            @Override
            public void execute(AdminPerformer performer, Arguments arguments) throws AdminException {

            }

            @Override
            public Set<Permission> permissions() {
                return null;
            }

            @Override
            public Arguments createArguments() {
                return new Arguments();
            }
        };

        assertFalse(hydrator.supports(command, null));
        assertTrue(hydrator.supports(command, new Arguments()));

        Arguments arguments = new Arguments();

        assertSame(arguments, hydrator.hydrate(command, arguments, new CommandParser.Arguments("", "", "", Arrays.asList("foo", "bar"), null)));
        assertEquals("bar", arguments.foo);
        assertFalse(arguments.bar);

        arguments = new Arguments();

        assertSame(arguments, hydrator.hydrate(command, arguments, new CommandParser.Arguments("", "", "", Arrays.asList("foo", "--bar", "bar"), null)));
        assertEquals("bar", arguments.foo);
        assertTrue(arguments.bar);

        assertThrows(CmdLineException.class, () -> hydrator.hydrate(command, new Arguments(), new CommandParser.Arguments("", "", "", Arrays.asList("foo"), null)));
    }

    @Test
    void help() throws Exception {
        Command<Arguments> command = new Command<Arguments>() {
            @Override
            public String name() {
                return "foo";
            }

            @Override
            public String description() {
                return null;
            }

            @Override
            public HelpFormatter help() {
                return new HelpFormatter(this);
            }

            @Override
            public void execute(AdminPerformer performer, Arguments arguments) throws AdminException {

            }

            @Override
            public Set<Permission> permissions() {
                return EnumSet.of(Permission.ACCESS);
            }

            @Override
            public Arguments createArguments() {
                return new Arguments();
            }
        };

        HelpFormatter help = hydrator.help(command, command.createArguments(), command.help());

        assertEquals(
            "foo - null\n" +
            "========================================\n" +
            "\n" +
            "<b>SYNOPSIS</b>\n" +
                "\tfoo FOO [DEF=42] [--bar] [--opt VAL=default]\n" +
            "\n" +
            "<b>OPTIONS</b>\n" +
                "\tFOO : My foo\n" +
                "\tDEF : Arg with default\n" +
                "\t--bar : Enable bar\n" +
                "\t--opt : Option with default\n" +
            "\n" +
            "<b>PERMISSIONS</b>\n" +
                "\t[ACCESS]",
            help.toString()
        );
    }
}
