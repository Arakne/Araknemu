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

package fr.quatrevieux.araknemu.game.handler.loader;

import fr.quatrevieux.araknemu.core.di.Container;
import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.core.network.parser.PacketHandler;
import fr.quatrevieux.araknemu.game.admin.AdminService;
import fr.quatrevieux.araknemu.game.handler.EnsureAdmin;
import fr.quatrevieux.araknemu.game.handler.basic.admin.ExecuteCommand;
import fr.quatrevieux.araknemu.network.game.GameSession;

/**
 * Loader for admin packets
 */
final public class AdminLoader extends AbstractLoader {
    public AdminLoader() {
        super(EnsureAdmin::new);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected PacketHandler<GameSession, ?>[] handlers(Container container) throws ContainerException {
        return new PacketHandler[] {
            new ExecuteCommand(
                container.get(AdminService.class)
            ),
        };
    }
}
