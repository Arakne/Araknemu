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

import fr.quatrevieux.araknemu.common.account.Permission;
import fr.quatrevieux.araknemu.game.account.GameAccount;
import fr.quatrevieux.araknemu.game.admin.exception.AdminException;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.Optional;
import java.util.Set;

/**
 * Perform admin operations
 */
public interface AdminPerformer extends AdminLogger {
    public static final Marker EXECUTE_MARKER = MarkerManager.getMarker("EXECUTE");
    public static final Marker OUTPUT_MARKER = MarkerManager.getMarker("OUTPUT");

    /**
     * Parse and execute a command
     *
     * @param command The command line
     *
     * @throws AdminException When an error occurs during executing the command
     */
    public void execute(String command) throws AdminException;

    /**
     * Check if the admin user has permissions
     *
     * @param permissions Set of required permissions
     */
    public boolean isGranted(Set<Permission> permissions);

    /**
     * Get the related account if exists
     */
    public Optional<GameAccount> account();
}
