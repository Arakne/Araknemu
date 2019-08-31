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

import fr.quatrevieux.araknemu.game.admin.exception.AdminException;
import fr.quatrevieux.araknemu.game.admin.exception.CommandException;
import fr.quatrevieux.araknemu.game.admin.exception.ContextNotFoundException;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Admin user parser for command line
 *
 * Examples:
 *
 * Simple command :
 * echo Hello World !
 *
 * Apply on self :
 * !getitem 456 2
 *
 * Child context :
 * >account ban 120s
 *
 * Named context :
 * $global info
 *
 * Anonymous context :
 * ${player:Robert} levelup 55
 *
 * Child anonymous context :
 * ${player:Robert} > account gift 45 3
 */
final public class AdminUserCommandParser implements CommandParser {
    final private AdminUser user;

    public AdminUserCommandParser(AdminUser user) {
        this.user = user;
    }

    @Override
    public Arguments parse(String line) throws AdminException {
        line = line.trim();

        if (line.isEmpty()) {
            throw new CommandException("Empty command");
        }

        AtomicReference<String> commandLine = new AtomicReference<>(line);

        Context context = parseContext(commandLine);
        String command = parseCommand(commandLine);
        List<String> arguments = parseArguments(commandLine);

        return new Arguments(
            command,
            arguments,
            context
        );
    }

    /**
     * Parse command arguments
     *
     * The first argument is the command name, and arguments are separated with white space
     */
    private List<String> parseArguments(AtomicReference<String> commandLine) {
        return Arrays.asList(
            StringUtils.split(commandLine.get(), " ")
        );
    }

    /**
     * Parse the command name
     *
     * The command name is the first argument of the command line
     */
    private String parseCommand(AtomicReference<String> commandLine) {
        return StringUtils.substringBefore(commandLine.get(), " ");
    }

    /**
     * Parse the command context
     *
     * If the line starts with !, the context is the current admin user
     * If the line stats with $, the context is dynamic
     *
     * After resolve the root context, resolve the child contexts separated by >
     */
    private Context parseContext(AtomicReference<String> commandLine) throws AdminException {
        String line = commandLine.get();
        Context context;

        switch (line.charAt(0)) {
            case '!':
                commandLine.set(line.substring(1));
                context = user.context().self();
                break;

            case '$':
                commandLine.set(line.substring(1));
                context = resolveDynamicContext(commandLine);
                break;

            default:
                context = user.context().current();
        }

        return resolveChildContext(commandLine, context);
    }

    /**
     * Resolve the child context
     * This method will be called recursively until no more child context is detected
     *
     * Child contexts are separated by >
     */
    private Context resolveChildContext(AtomicReference<String> commandLine, Context context) throws ContextNotFoundException {
        String line = StringUtils.stripStart(commandLine.get(), null);

        if (line.charAt(0) != '>') {
            return context;
        }

        line = StringUtils.stripStart(line.substring(1).trim(), null);

        int end = 0;

        while (end < line.length() && Character.isLetterOrDigit(line.charAt(end))) {
            ++end;
        }

        String name = line.substring(0, end);
        commandLine.set(line.substring(end + 1));

        return resolveChildContext(commandLine, context.child(name));
    }

    /**
     * Resolve a dynamic context (start with $)
     *
     * If the line starts with {, try to resolve an anonymous context
     * Else, get an already registered context
     */
    private Context resolveDynamicContext(AtomicReference<String> commandLine) throws AdminException {
        String line = commandLine.get();

        if (line.charAt(0) == '{') {
            commandLine.set(line.substring(1));

            return resolveAnonymousContext(commandLine);
        }

        int end = 0;

        while (end < line.length() && Character.isLetterOrDigit(line.charAt(end))) {
            ++end;
        }

        String name = line.substring(0, end);
        commandLine.set(line.substring(end + 1));

        return user.context().get(name);
    }

    /**
     * Resolve the anonymous context between accolades
     *
     * The anonymous context is in form : ${type:argument}
     */
    private Context resolveAnonymousContext(AtomicReference<String> commandLine) throws AdminException {
        String line = commandLine.get();

        int end = 0;

        while (end < line.length() && line.charAt(end) != '}') {
            ++end;
        }

        if (end == line.length()) {
            throw new CommandException("Syntax error : missing closing accolade");
        }

        String[] arguments = StringUtils.split(line.substring(0, end), ":", 2);
        commandLine.set(
            StringUtils.stripStart(line.substring(end + 1), " ")
        );

        return user.context().resolve(
            arguments[0],
            arguments.length == 1 ? "" : arguments[1]
        );
    }
}
