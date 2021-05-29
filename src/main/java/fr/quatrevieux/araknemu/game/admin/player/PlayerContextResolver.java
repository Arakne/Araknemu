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

import fr.quatrevieux.araknemu.game.admin.AdminPerformer;
import fr.quatrevieux.araknemu.game.admin.account.AccountContextResolver;
import fr.quatrevieux.araknemu.game.admin.context.Context;
import fr.quatrevieux.araknemu.game.admin.context.AbstractContextConfigurator;
import fr.quatrevieux.araknemu.game.admin.context.ContextResolver;
import fr.quatrevieux.araknemu.game.admin.exception.ContextException;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.player.PlayerService;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Supplier;

/**
 * Context resolver for player
 */
public final class PlayerContextResolver implements ContextResolver {
    private final PlayerService service;
    private final AccountContextResolver accountContextResolver;

    private final List<AbstractContextConfigurator<PlayerContext>> configurators = new ArrayList<>();

    public PlayerContextResolver(PlayerService service, AccountContextResolver accountContextResolver) {
        this.service = service;
        this.accountContextResolver = accountContextResolver;
    }

    @Override
    public Context resolve(AdminPerformer performer, Supplier<String> argument) throws ContextException {
        final String name = argument.get();

        try {
            return resolve(service.get(name));
        } catch (NoSuchElementException e) {
            throw new ContextException("Cannot found the player " + name);
        }
    }

    @Override
    public char prefix() {
        return '@';
    }

    /**
     * Register a new configurator for the player context
     */
    public PlayerContextResolver register(AbstractContextConfigurator<PlayerContext> configurator) {
        configurators.add(configurator);

        return this;
    }

    /**
     * Create the context from the given player instance
     *
     * @param player The player instance
     *
     * @return The created context
     */
    public PlayerContext resolve(GamePlayer player) throws ContextException {
        return new PlayerContext(
            player,
            accountContextResolver.resolve(player.account()),
            configurators
        );
    }
}
