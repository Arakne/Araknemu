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

package fr.quatrevieux.araknemu.game.admin.player;

import fr.quatrevieux.araknemu.game.admin.Context;
import fr.quatrevieux.araknemu.game.admin.ContextResolver;
import fr.quatrevieux.araknemu.game.admin.exception.ContextException;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.exploration.map.GeolocationService;
import fr.quatrevieux.araknemu.game.item.ItemService;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.player.PlayerService;

import java.util.NoSuchElementException;

/**
 * Context resolver for player
 */
final public class PlayerContextResolver implements ContextResolver {
    final private PlayerService service;
    final private ContextResolver accountContextResolver;
    final private ItemService itemService;
    final private GeolocationService geolocationService;
    final private ExplorationMapService mapService;

    public PlayerContextResolver(PlayerService service, ContextResolver accountContextResolver, ItemService itemService, GeolocationService geolocationService, ExplorationMapService mapService) {
        this.service = service;
        this.accountContextResolver = accountContextResolver;
        this.itemService = itemService;
        this.geolocationService = geolocationService;
        this.mapService = mapService;
    }

    @Override
    public Context resolve(Context globalContext, Object argument) throws ContextException {
        if (argument instanceof GamePlayer) {
            return resolve(globalContext, GamePlayer.class.cast(argument));
        } else if (argument instanceof String) {
            return resolve(globalContext, String.class.cast(argument));
        }

        throw new ContextException("Invalid argument : " + argument);
    }

    @Override
    public String type() {
        return "player";
    }

    private PlayerContext resolve(Context globalContext, GamePlayer player) throws ContextException {
        return new PlayerContext(
            player,
            accountContextResolver.resolve(globalContext, player.account()),
            itemService,
            geolocationService,
            mapService,
            service
        );
    }

    private PlayerContext resolve(Context globalContext, String name) throws ContextException {
        try {
            return resolve(globalContext, service.get(name));
        } catch (NoSuchElementException e) {
            throw new ContextException("Cannot found the player " + name);
        }
    }
}
