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

import fr.quatrevieux.araknemu.core.dbal.repository.EntityNotFoundException;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;

/**
 * Resolve the target map using the map id
 */
public final class MapResolver implements LocationResolver {
    private final ExplorationMapService service;

    public MapResolver(ExplorationMapService service) {
        this.service = service;
    }

    @Override
    public String name() {
        return "map";
    }

    @Override
    public void resolve(String argument, Target target) {
        final int mapId = Integer.parseUnsignedInt(argument);

        try {
            target.setMap(service.load(mapId));
        } catch (EntityNotFoundException e) {
            throw new IllegalArgumentException("The map " + argument + " cannot be found");
        }
    }

    @Override
    public String help() {
        return "Resolve by map id.\nUsage: goto map [mapid]";
    }
}
