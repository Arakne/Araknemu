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
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.player.PlayerService;

import java.util.NoSuchElementException;

/**
 * Use the player's current map as target map
 */
public final class PlayerResolver implements LocationResolver {
    private final PlayerService playerService;
    private final ExplorationMapService mapService;

    public PlayerResolver(PlayerService playerService, ExplorationMapService mapService) {
        this.playerService = playerService;
        this.mapService = mapService;
    }

    @Override
    public String name() {
        return "player";
    }

    @Override
    public TeleportationTarget resolve(String argument, TeleportationTarget target) {
        try {
            final GamePlayer player = playerService.get(argument);
            final ExplorationMap map = player.isExploring() ? player.exploration().map() : null;

            if (map != null) {
                return target.withMap(map);
            }

            return target.withMap(mapService.load(player.position().map()));
        } catch (NoSuchElementException e) {
            throw new IllegalArgumentException("The player " + argument + " cannot be found");
        }
    }

    @Override
    public String help() {
        return "Teleport to the player.\nUsage: goto player [name]";
    }
}
