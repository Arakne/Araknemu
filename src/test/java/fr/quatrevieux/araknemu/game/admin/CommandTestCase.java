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

package fr.quatrevieux.araknemu.game.admin;

import fr.quatrevieux.araknemu.common.account.Permission;
import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.account.GameAccount;
import fr.quatrevieux.araknemu.game.admin.context.Context;
import fr.quatrevieux.araknemu.game.admin.exception.AdminException;
import fr.quatrevieux.araknemu.game.admin.executor.CommandExecutor;
import fr.quatrevieux.araknemu.game.admin.executor.argument.ArgumentsHydrator;
import fr.quatrevieux.araknemu.util.LogFormatter;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

abstract public class CommandTestCase extends GameBaseCase {
    protected Command command;
    protected PerformerWrapper performer;

    static public class PerformerWrapper implements AdminPerformer {
        public class Entry {
            final public LogType type;
            final public String message;

            public Entry(LogType type, String message) {
                this.type = type;
                this.message = message;
            }
        }

        final private AdminPerformer performer;
        public List<Entry> logs = new ArrayList<>();

        public PerformerWrapper(AdminPerformer performer) {
            this.performer = performer;
        }

        @Override
        public void execute(String command) throws AdminException {
            performer.execute(command);
        }

        @Override
        public boolean isGranted(Set<Permission> permissions) {
            return true;
        }

        @Override
        public Optional<GameAccount> account() {
            return performer.account();
        }

        @Override
        public Context self() {
            return performer.self();
        }

        @Override
        public void log(LogType type, String message, Object... arguments) {
            logs.add(
                new Entry(
                    type,
                    LogFormatter.format(message, arguments)
                )
            );
        }
    }

    public AdminUser user() throws ContainerException, SQLException, AdminException {
        return container.get(AdminSessionService.class).user(gamePlayer(true));
    }

    public void execute(AdminPerformer performer, String... arguments) throws AdminException {
        try {
            this.performer = new PerformerWrapper(performer);
            final CommandParser.Arguments parsedArgs = new CommandParser.Arguments("", "", command.name(), Arrays.asList(arguments), user().self());

            container.get(CommandExecutor.class).execute(command, this.performer, parsedArgs);
        } catch (SQLException e) {
            throw new AdminException(e);
        }
    }

    public void execute(String... arguments) throws ContainerException, SQLException, AdminException {
        execute(user(), arguments);
    }

    public void executeWithAdminUser(String... arguments) throws AdminException {
        try {
            final AdminUser performer = user();
            performer.account().get().grant(Permission.values());

            final CommandParser.Arguments parsedArgs = new CommandParser.Arguments("", "", command.name(), Arrays.asList(arguments), user().self());

            container.get(CommandExecutor.class).execute(command, user(), parsedArgs);
        } catch (SQLException e) {
            throw new AdminException(e);
        }
    }

    public void executeLine(String line) throws AdminException, SQLException {
        performer = new PerformerWrapper(user());
        command.execute(performer, container.get(CommandParser.class).parse(user(), line));
    }

    public void assertOutput(String... lines) {
        assertArrayEquals(
            lines,
            performer.logs
                .stream()
                .map(entry -> entry.message)
                .toArray()
        );
    }

    public void assertOutputContains(String line) {
        assertContains(
            line,
            performer.logs
                .stream()
                .map(entry -> entry.message)
                .collect(Collectors.toList())
        );
    }

    public void assertSuccess(String line) {
        assertLogType(LogType.SUCCESS, line);
    }

    public void assertError(String line) {
        assertLogType(LogType.ERROR, line);
    }

    public void assertInfo(String line) {
        assertLogType(LogType.DEFAULT, line);
    }

    public void assertLogType(LogType type, String line) {
        Optional<PerformerWrapper.Entry> log = performer.logs
            .stream()
            .filter(entry -> entry.message.equals(line))
            .findFirst()
        ;

        assertTrue(log.isPresent(), () -> "Line not found in logs : " + line + "\nActual : \n" + performer.logs.stream().map(entry -> entry.message).collect(Collectors.joining("\n")));
        assertEquals(type, log.get().type, "Invalid log type");
    }

    public String commandHelp() {
        return container.get(ArgumentsHydrator.class).help(command, command.createArguments(), command.help()).toString();
    }

    public void assertHelp(String ...lines) {
        String help = commandHelp().replaceAll("(<.*?>)", "");

        assertEquals(
            Arrays.stream(lines).filter(s -> !s.isEmpty()).collect(Collectors.joining("\n")),
            help.replace("\n\n", "\n")
        );
    }
}
