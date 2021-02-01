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

package fr.quatrevieux.araknemu.game.handler.basic.admin;

import fr.quatrevieux.araknemu.core.network.exception.ErrorPacket;
import fr.quatrevieux.araknemu.core.network.parser.PacketHandler;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.move.ChangeMap;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.GeolocationService;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.game.in.basic.admin.AdminMove;
import fr.quatrevieux.araknemu.network.game.out.basic.Noop;

/**
 * Go to the requested geolocation
 */
public final class GoToGeolocation implements PacketHandler<GameSession, AdminMove> {
    private final GeolocationService service;

    public GoToGeolocation(GeolocationService service) {
        this.service = service;
    }

    @Override
    public void handle(GameSession session, AdminMove packet) {
        if (!session.account().isMaster()) {
            throw new ErrorPacket(new Noop());
        }

        final ExplorationPlayer player = session.exploration();
        final ExplorationMap target = service.find(packet.geolocation(), GeolocationService.GeolocationContext.fromMap(player.map()));

        if (player.position().cell() < target.size() && target.get(player.position().cell()).walkable()) {
            player.interactions().push(new ChangeMap(player, target, player.position().cell()));
            return;
        }

        // @todo utility class for find valid cell ?
        for (int cellId = 0; cellId < target.size(); ++cellId) {
            if (target.get(cellId).walkable()) {
                player.interactions().push(new ChangeMap(player, target, cellId));
                return;
            }
        }
    }

    @Override
    public Class<AdminMove> packet() {
        return AdminMove.class;
    }
}
