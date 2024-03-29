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

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.admin.exception.AdminException;
import fr.quatrevieux.araknemu.game.handler.event.Disconnected;
import fr.quatrevieux.araknemu.game.player.GamePlayer;

import java.util.HashMap;
import java.util.Map;

/**
 * Handle admin sessions
 */
public final class AdminSessionService {
    private final AdminUser.Factory userFactory;

    private final Map<Integer, AdminUser> usersById = new HashMap<>();

    public AdminSessionService(AdminUser.Factory userFactory) {
        this.userFactory = userFactory;
    }

    /**
     * Get an admin user from player
     *
     * @param player The admin player
     */
    public AdminUser user(GamePlayer player) throws AdminException {
        AdminUser user = usersById.get(player.id());

        if (user == null) {
            usersById.put(player.id(), user = createAdminUserSession(player));
        }

        return user;
    }

    private AdminUser createAdminUserSession(GamePlayer player) throws AdminException {
        final AdminUser adminUser = userFactory.create(player);

        adminUser.player().dispatcher().add(new Listener<Disconnected>() {
            @Override
            public void on(Disconnected event) {
                usersById.remove(player.id());
            }

            @Override
            public Class<Disconnected> event() {
                return Disconnected.class;
            }
        });

        return adminUser;
    }
}
