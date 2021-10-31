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

package fr.quatrevieux.araknemu.game.admin.executor.argument.type;

import fr.quatrevieux.araknemu.game.admin.AdminPerformer;
import fr.quatrevieux.araknemu.game.admin.Command;
import fr.quatrevieux.araknemu.game.admin.exception.AdminException;

/**
 * Base type for {@link org.kohsuke.args4j.spi.SubCommands} system
 * When use the type, declaring {@link org.kohsuke.args4j.spi.SubCommandHandler} as handler is not necessary
 *
 * This interface follow the double dispatch pattern for call the method corresponding the current argument
 */
public interface SubArguments<C extends Command> {
    /**
     * Execute the command with the current arguments
     *
     * @param performer The command perform
     * @param command Command to execute
     */
    public void execute(AdminPerformer performer, C command) throws AdminException;
}
