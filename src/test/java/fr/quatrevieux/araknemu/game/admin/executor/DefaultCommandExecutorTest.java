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

package fr.quatrevieux.araknemu.game.admin.executor;

import fr.quatrevieux.araknemu.common.account.Permission;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.admin.AbstractCommand;
import fr.quatrevieux.araknemu.game.admin.AdminPerformer;
import fr.quatrevieux.araknemu.game.admin.AdminService;
import fr.quatrevieux.araknemu.game.admin.Command;
import fr.quatrevieux.araknemu.game.admin.CommandParser;
import fr.quatrevieux.araknemu.game.admin.CommandTestCase;
import fr.quatrevieux.araknemu.game.admin.exception.AdminException;
import fr.quatrevieux.araknemu.game.admin.exception.CommandException;
import fr.quatrevieux.araknemu.game.admin.exception.CommandPermissionsException;
import fr.quatrevieux.araknemu.game.admin.executor.argument.HydratorsAggregate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kohsuke.args4j.Argument;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class DefaultCommandExecutorTest extends GameBaseCase {
    private DefaultCommandExecutor executor;
    private AdminPerformer performer;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        executor = new DefaultCommandExecutor(new HydratorsAggregate());
        performer = container.get(AdminService.class).user(gamePlayer());

        gamePlayer().account().grant(Permission.ACCESS);
    }

    @Test
    void notGranted() throws AdminException {
        Command<Void> command = new AbstractCommand<Void>() {
            @Override
            protected void build(Builder builder) {
                builder.requires(Permission.SUPER_ADMIN);
            }

            @Override
            public String name() {
                return "foo";
            }

            @Override
            public void execute(AdminPerformer performer, Void arguments) throws AdminException {}
        };

        assertThrows(CommandPermissionsException.class, () -> executor.execute(command, performer, new CommandParser.Arguments("", "", "", Arrays.asList("foo", "bar"), null)));
    }

    @Test
    void notSupportedArgument() throws AdminException {
        class MyCommand extends AbstractCommand<Object> {
            @Override
            protected void build(Builder builder) {
            }

            @Override
            public String name() {
                return "foo";
            }

            @Override
            public void execute(AdminPerformer performer, Object arguments) throws AdminException {}
        }

        Command<Object> command = new MyCommand();

        assertThrowsWithMessage(CommandException.class, "Cannot parse arguments for command MyCommand", () -> executor.execute(command, performer, new CommandParser.Arguments("", "", "", Arrays.asList("foo", "bar"), null)));
    }

    @Test
    void hydratorAdminException() throws AdminException {
        class MyCommand extends AbstractCommand<String> {
            @Override
            protected void build(Builder builder) {
            }

            @Override
            public String name() {
                return "foo";
            }

            @Override
            public void execute(AdminPerformer performer, String arguments) throws AdminException {}
        }

        Command<String> command = new MyCommand();

        assertThrowsWithMessage(CommandException.class, "Argument is missing", () -> executor.execute(command, performer, new CommandParser.Arguments("", "", "", Arrays.asList("foo"), null)));
    }

    @Test
    void hydratorOtherException() throws AdminException {
        class MyCommand extends AbstractCommand<MyCommand.Arguments> {
            @Override
            protected void build(Builder builder) {
            }

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

            class Arguments {
                @Argument(required = true, metaVar = "foo")
                public String foo;
            }
        }

        MyCommand command = new MyCommand();

        assertThrowsWithMessage(CommandException.class, "Argument \"foo\" is required", () -> executor.execute(command, performer, new CommandParser.Arguments("", "", "", Arrays.asList("foo"), null)));
    }

    @Test
    void executeSuccess() throws AdminException {
        class MyCommand extends AbstractCommand<MyCommand.Arguments> {
            public Arguments arguments;
            public AdminPerformer performer;

            @Override
            protected void build(Builder builder) {
            }

            @Override
            public String name() {
                return "foo";
            }

            @Override
            public void execute(AdminPerformer performer, Arguments arguments) throws AdminException {
                this.performer = performer;
                this.arguments = arguments;
            }

            @Override
            public Arguments createArguments() {
                return new Arguments();
            }

            class Arguments {
                @Argument
                public String foo;
            }
        }

        MyCommand command = new MyCommand();

        executor.execute(command, performer, new CommandParser.Arguments("", "", "", Arrays.asList("foo", "bar"), null));

        assertEquals("bar", command.arguments.foo);
        assertSame(performer, command.performer);
    }
}
