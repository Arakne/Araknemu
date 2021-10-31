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
import fr.quatrevieux.araknemu.game.admin.CommandParser;
import fr.quatrevieux.araknemu.game.admin.exception.CommandException;
import fr.quatrevieux.araknemu.game.admin.exception.ExceptionHandler;
import fr.quatrevieux.araknemu.game.admin.formatter.Link;

/**
 * Exception handler for base command exception
 */
public final class CommandExceptionHandler implements ExceptionHandler.Function<CommandException> {
    @Override
    public void handle(AdminPerformer performer, CommandException error, CommandParser.Arguments arguments) {
        performer.error("An error occurs during execution of '{}' : {}", error.command(), error.getMessage());
        performer.error(
            "See {} for usage",
            (new Link())
                .execute((arguments != null ? arguments.contextPath() : "") + " help " + error.command())
                .text("help")
        );
    }
}
