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
 * Copyright (c) 2017-2020 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.admin;

import fr.quatrevieux.araknemu.game.admin.exception.AdminException;

import java.util.List;

/**
 * Parser for console command line
 */
public interface CommandParser {
    final public class Arguments {
        final private String line;
        final private String contextPath;
        final private String command;
        final private List<String> arguments;
        final private Context context;

        public Arguments(String line, String contextPath, String command, List<String> arguments, Context context) {
            this.line = line;
            this.contextPath = contextPath;
            this.command = command;
            this.arguments = arguments;
            this.context = context;
        }

        /**
         * Get the raw command line
         */
        public String line() {
            return line;
        }

        /**
         * Get the raw context part of the command (before command)
         */
        public String contextPath() {
            return contextPath;
        }

        /**
         * Get the command name
         */
        public String command() {
            return command;
        }

        /**
         * Get the command arguments
         * Note: the first element (index 0) always contains the command name
         */
        public List<String> arguments() {
            return arguments;
        }

        /**
         * Get the resolved context
         */
        public Context context() {
            return context;
        }
    }

    /**
     * Parse a command line
     */
    public Arguments parse(String line) throws AdminException;
}
