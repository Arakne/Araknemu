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

import java.util.List;

/**
 * Parser for console command line
 */
public interface CommandParser {
    final public class Arguments {
        final private String command;
        final private List<String> arguments;
        final private Context context;

        public Arguments(String command, List<String> arguments, Context context) {
            this.command = command;
            this.arguments = arguments;
            this.context = context;
        }

        public String command() {
            return command;
        }

        public List<String> arguments() {
            return arguments;
        }

        public Context context() {
            return context;
        }
    }

    /**
     * Parse a command line
     */
    public Arguments parse(String line) throws AdminException;
}
