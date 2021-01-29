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

import fr.quatrevieux.araknemu.common.account.Permission;

import java.util.Set;

/**
 * Required permissions is not granted for execute the command
 */
public class CommandPermissionsException extends CommandException {
    final private Set<Permission> permissions;

    public CommandPermissionsException(String command, Set<Permission> permissions) {
        super(command);
        this.permissions = permissions;
    }

    /**
     * Get the required permissions to execute the command
     *
     * @see fr.quatrevieux.araknemu.game.admin.Command#permissions()
     */
    final public Set<Permission> permissions() {
        return permissions;
    }
}
