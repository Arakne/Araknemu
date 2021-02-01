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

package fr.quatrevieux.araknemu.game.admin.player;

import fr.quatrevieux.araknemu.game.admin.context.Context;
import fr.quatrevieux.araknemu.game.admin.context.AbstractContextConfigurator;
import fr.quatrevieux.araknemu.game.admin.context.ContextResolver;
import fr.quatrevieux.araknemu.game.admin.exception.ContextException;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.player.PlayerService;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Context resolver for player
 */
public final class PlayerContextResolver implements ContextResolver {
    private final PlayerService service;
    private final ContextResolver accountContextResolver;

    private final List<AbstractContextConfigurator<PlayerContext>> configurators = new ArrayList<>();

    public PlayerContextResolver(PlayerService service, ContextResolver accountContextResolver) {
        this.service = service;
        this.accountContextResolver = accountContextResolver;
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

    /**
     * Register a new configurator for the player context
     */
    public PlayerContextResolver register(AbstractContextConfigurator<PlayerContext> configurator) {
        configurators.add(configurator);

        return this;
    }

    private PlayerContext resolve(Context globalContext, GamePlayer player) throws ContextException {
        return new PlayerContext(
            player,
            accountContextResolver.resolve(globalContext, player.account()),
            configurators
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
