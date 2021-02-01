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

/**
 * Logger for admin console
 *
 * The log message can be formatted using "{}" placeholder.
 *
 * To log a simple success message :
 * logger.success("Command successfully executed");
 *
 * Log an error with an argument :
 * logger.error("The argument {} is not valid", arg);
 *
 * Passing multiple arguments
 * logger.info("Hello {}, my name is {} and I'm {} y-o", "John", "Alan", 36);
 */
public interface AdminLogger {
    /**
     * Log to admin console
     *
     * @param type The log type
     * @param message The log message
     * @param arguments The message arguments
     */
    public void log(LogType type, String message, Object... arguments);

    /**
     * Log an information message (white)
     *
     * @param message Message to log
     * @param arguments Message arguments
     */
    public default void info(String message, Object... arguments) {
        log(LogType.DEFAULT, message, arguments);
    }

    /**
     * Log an error message (red)
     *
     * @param message Message to log
     * @param arguments Message arguments
     */
    public default void error(String message, Object... arguments) {
        log(LogType.ERROR, message, arguments);
    }

    /**
     * Log a success message (green)
     *
     * @param message Message to log
     * @param arguments Message arguments
     */
    public default void success(String message, Object... arguments) {
        log(LogType.SUCCESS, message, arguments);
    }
}
