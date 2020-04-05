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

package fr.quatrevieux.araknemu.game.admin;

import fr.quatrevieux.araknemu.game.admin.context.Context;
import fr.quatrevieux.araknemu.game.admin.context.ContextResolver;
import fr.quatrevieux.araknemu.game.admin.exception.ContextException;
import fr.quatrevieux.araknemu.game.player.GamePlayer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Server for admin
 */
final public class AdminService {
    final private Context globalContext;
    final private Map<String, ContextResolver> resolvers;

    final private Map<Integer, AdminUser> usersById = new HashMap<>();

    public AdminService(Context globalContext, Collection<ContextResolver> resolvers) {
        this.globalContext = globalContext;
        this.resolvers = resolvers
            .stream()
            .collect(
                Collectors.toMap(
                    ContextResolver::type,
                    Function.identity()
                )
            )
        ;
    }

    /**
     * Get an admin user from player
     *
     * @param player The admin player
     */
    public AdminUser user(GamePlayer player) throws ContextException {
        if (!usersById.containsKey(player.id())) {
            usersById.put(
                player.id(),
                new AdminUser(this, player)
            );
        }

        return usersById.get(player.id());
    }

    /**
     * Resolve a context
     *
     * For resolve a player :
     * resolve("player", "PlayerName");
     *
     * @param type The context type name
     * @param argument The context argument
     *
     * @return The resolved context
     * @throws ContextException When the context type cannot be found
     */
    public Context context(String type, Object argument) throws ContextException {
        if (!resolvers.containsKey(type)) {
            throw new ContextException("Context type '" + type + "' not found");
        }

        return resolvers.get(type).resolve(globalContext, argument);
    }

    /**
     * Remove the admin session
     */
    void removeSession(AdminUser user) {
        usersById.remove(user.id());
    }
}
