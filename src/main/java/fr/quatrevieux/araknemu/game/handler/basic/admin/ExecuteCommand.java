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

package fr.quatrevieux.araknemu.game.handler.basic.admin;

import fr.quatrevieux.araknemu.core.network.exception.CloseImmediately;
import fr.quatrevieux.araknemu.game.admin.AdminSessionService;
import fr.quatrevieux.araknemu.game.admin.AdminUser;
import fr.quatrevieux.araknemu.game.handler.AbstractPlayingPacketHandler;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.game.in.basic.admin.AdminCommand;

/**
 * Execute an admin command
 */
public final class ExecuteCommand extends AbstractPlayingPacketHandler<AdminCommand> {
    private final AdminSessionService service;

    public ExecuteCommand(AdminSessionService service) {
        this.service = service;
    }

    @Override
    public void handle(GameSession session, GamePlayer player, AdminCommand packet) throws Exception {
        if (!player.account().isMaster()) {
            throw new CloseImmediately("Admin account required");
        }

        final AdminUser user = service.user(player);

        try {
            user.execute(packet.command());
        } catch (Exception e) {
            user.error(e);
        }
    }

    @Override
    public Class<AdminCommand> packet() {
        return AdminCommand.class;
    }
}
