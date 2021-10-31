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

import fr.quatrevieux.araknemu.game.admin.AdminPerformer;
import fr.quatrevieux.araknemu.game.admin.Command;
import fr.quatrevieux.araknemu.game.admin.CommandParser;
import fr.quatrevieux.araknemu.game.admin.exception.CommandNotFoundException;
import fr.quatrevieux.araknemu.game.admin.exception.ExceptionHandler;
import fr.quatrevieux.araknemu.game.admin.formatter.Link;
import org.apache.commons.text.similarity.LevenshteinDistance;

import java.util.Comparator;
import java.util.Optional;

/**
 * Handler for command not found
 * This handler will try to found the nearest command name on the current context
 */
public final class CommandNotFoundExceptionHandler implements ExceptionHandler.Function<CommandNotFoundException> {
    @Override
    public void handle(AdminPerformer performer, CommandNotFoundException error, CommandParser.Arguments arguments) {
        performer.error("Command '{}' is not found", error.command());

        if (arguments == null) {
            return;
        }

        nearestCommandName(arguments).ifPresent(commandName -> performer.error(
            "Did you mean {} ? (See {})",
            new Link().text(commandName).command(rebuildCommand(arguments, commandName)),
            new Link().text("help").execute(arguments.contextPath() + " help " + commandName)
        ));
    }

    /**
     * Found the nearest command name from the contexts
     *
     * @param arguments The parsed arguments
     *
     * @return The command name
     */
    private Optional<String> nearestCommandName(CommandParser.Arguments arguments) {
        return arguments.context().commands().stream()
            .map(Command::name)
            .min(Comparator.comparingInt(name -> LevenshteinDistance.getDefaultInstance().apply(arguments.command(), name)))
        ;
    }

    /**
     * Change the command name from the current command line
     *
     * @param arguments The parsed arguments
     * @param newName The expected command name
     *
     * @return The new command line
     */
    private String rebuildCommand(CommandParser.Arguments arguments, String newName) {
        final StringBuilder sb = new StringBuilder();

        sb.append(arguments.contextPath());
        sb.append(' ');
        sb.append(newName);

        for (int i = 1; i < arguments.arguments().size(); ++i) {
            sb.append(' ').append(arguments.arguments().get(i));
        }

        return sb.toString();
    }
}
