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

package fr.quatrevieux.araknemu.game.admin.player.teleport;

import fr.quatrevieux.araknemu.game.exploration.interaction.map.TeleportationTarget;

/**
 * Resolver for goto command argument
 */
public interface LocationResolver {
    /**
     * The resolver name
     */
    public String name();

    /**
     * Resolve the target
     *
     * @param argument The command argument
     * @param target The location target to fill
     *
     * @return The filled target
     *
     * @throws IllegalArgumentException When the argument is invalid for the resolver
     */
    public TeleportationTarget resolve(String argument, TeleportationTarget target);

    /**
     * Get the resolver help
     */
    public String help();
}
