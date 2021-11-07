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

import fr.quatrevieux.araknemu.game.admin.Command;
import fr.quatrevieux.araknemu.game.admin.CommandParser;
import fr.quatrevieux.araknemu.game.admin.exception.CommandException;

/**
 * Handle single string argument
 * If the argument is missing and no default value is provided on "createArgument" method, an error will be thrown
 */
public final class StringHydrator extends AbstractTypedArgumentsHydrator<String> {
    @Override
    protected String typedHydrate(Command<String> command, String commandArguments, CommandParser.Arguments parsedArguments) throws CommandException {
        if (parsedArguments.arguments().size() < 2) {
            // Use the argument as default value
            if (commandArguments != null) {
                return commandArguments;
            }

            throw new CommandException(command.name(), "Argument is missing"); // @todo custom exception
        }

        // Get the first argument value
        return parsedArguments.arguments().get(1);
    }

    @Override
    protected Class<String> type() {
        return String.class;
    }
}
