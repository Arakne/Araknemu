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
import fr.quatrevieux.araknemu.data.value.Geolocation;
import fr.quatrevieux.araknemu.game.exploration.interaction.map.TeleportationTarget;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.GeolocationService;
import fr.quatrevieux.araknemu.game.player.GamePlayer;

/**
 * Resolve the target map using a geolocation
 *
 * @todo handle area
 */
public final class PositionResolver implements LocationResolver {
    private final GamePlayer player;
    private final GeolocationService service;

    public PositionResolver(GamePlayer player, GeolocationService service) {
        this.player = player;
        this.service = service;
    }

    @Override
    public String name() {
        return "position";
    }

    @Override
    public TeleportationTarget resolve(String argument, TeleportationTarget target) {
        final String[] parts = argument.split("[;,]", 2);

        // @todo area ?
        if (parts.length != 2) {
            throw new IllegalArgumentException("Malformed position : must be in format x;y");
        }

        final ExplorationMap currentMap = player.isExploring() ? player.exploration().map() : null;

        try {
            return target.withMap(service.find(
                new Geolocation(
                    Integer.parseInt(parts[0]),
                    Integer.parseInt(parts[1])
                ),
                currentMap != null ? GeolocationService.GeolocationContext.fromMap(currentMap) : new GeolocationService.GeolocationContext()
            ));
        } catch (EntityNotFoundException e) {
            throw new IllegalArgumentException("Cannot found map at position " + argument);
        }
    }

    @Override
    public String help() {
        return "Resolve by map position.\nUsage: goto position [x];[y]";
    }
}
