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

package fr.quatrevieux.araknemu.game.admin.exception;

/**
 * Base exception for commands
 */
public class CommandException extends AdminException {
    final private String command;

    public CommandException(String command) {
        this.command = command;
    }

    public CommandException(String command, String message) {
        super(message);
        this.command = command;
    }

    public CommandException(String command, String message, Throwable cause) {
        super(message, cause);
        this.command = command;
    }

    public CommandException(String command, Throwable cause) {
        super(cause);
        this.command = command;
    }

    /**
     * The failing command name
     *
     * @see fr.quatrevieux.araknemu.game.admin.Command#name()
     */
    final public String command() {
        return command;
    }
}
