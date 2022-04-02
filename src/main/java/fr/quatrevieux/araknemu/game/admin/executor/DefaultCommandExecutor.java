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

import fr.quatrevieux.araknemu.game.admin.AdminPerformer;
import fr.quatrevieux.araknemu.game.admin.Command;
import fr.quatrevieux.araknemu.game.admin.CommandParser;
import fr.quatrevieux.araknemu.game.admin.exception.AdminException;
import fr.quatrevieux.araknemu.game.admin.exception.CommandException;
import fr.quatrevieux.araknemu.game.admin.exception.CommandPermissionsException;
import fr.quatrevieux.araknemu.game.admin.executor.argument.ArgumentsHydrator;

/**
 * Base executor using {@link ArgumentsHydrator}
 */
public final class DefaultCommandExecutor implements CommandExecutor {
    private final ArgumentsHydrator hydrator;

    public DefaultCommandExecutor(ArgumentsHydrator hydrator) {
        this.hydrator = hydrator;
    }

    @Override
    @SuppressWarnings("type.argument")
    public <A> void execute(Command<A> command, AdminPerformer performer, CommandParser.Arguments arguments) throws AdminException {
        if (!performer.isGranted(command.permissions())) {
            throw new CommandPermissionsException(command.name(), command.permissions());
        }

        A args = command.createArguments();

        if (!hydrator.supports(command, args)) {
            throw new CommandException(command.name(), "Cannot parse arguments for command " + command.getClass().getSimpleName());
        }

        try {
            args = hydrator.hydrate(command, args, arguments);
        } catch (AdminException ae) {
            throw ae;
        } catch (Exception e) {
            throw new CommandException(command.name(), e.getMessage() != null ? e.getMessage() : e.toString());
        }

        command.execute(performer, args);
    }
}
