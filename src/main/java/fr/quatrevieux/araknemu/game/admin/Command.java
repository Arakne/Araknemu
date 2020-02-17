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

import fr.quatrevieux.araknemu.common.account.Permission;
import fr.quatrevieux.araknemu.game.admin.exception.AdminException;

import java.util.List;
import java.util.Set;

/**
 * A console command
 */
public interface Command {
    /**
     * Get the command name
     */
    public String name();

    /**
     * Get a simple description for the command
     */
    public String description();

    /**
     * Get the help message
     */
    public String help();

    /**
     * Execute the command
     *
     * @param performer The command performer
     * @param arguments The command arguments
     */
    public void execute(AdminPerformer performer, CommandParser.Arguments arguments) throws AdminException;

    /**
     * Get list of required permissions
     */
    public Set<Permission> permissions();
}
