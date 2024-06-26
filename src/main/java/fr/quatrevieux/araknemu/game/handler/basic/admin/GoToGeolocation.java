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
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.move.TeleportationTarget;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.GeolocationService;
import fr.quatrevieux.araknemu.game.handler.AbstractExploringPacketHandler;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.game.in.basic.admin.AdminMove;
import fr.quatrevieux.araknemu.network.game.out.basic.Noop;

/**
 * Go to the requested geolocation
 */
public final class GoToGeolocation extends AbstractExploringPacketHandler<AdminMove> {
    private final GeolocationService service;

    public GoToGeolocation(GeolocationService service) {
        this.service = service;
    }

    @Override
    public void handle(GameSession session, ExplorationPlayer exploration, AdminMove packet) {
        if (!exploration.account().isMaster()) {
            throw new ErrorPacket(new Noop());
        }

        final ExplorationMap currentMap = exploration.map();
        final ExplorationMap targetMap = service.find(
            packet.geolocation(),
            currentMap != null
                ? GeolocationService.GeolocationContext.fromMap(currentMap)
                : new GeolocationService.GeolocationContext()
        );

        new TeleportationTarget(targetMap, exploration.cell().id())
            .ensureCellWalkable()
            .apply(exploration)
        ;
    }

    @Override
    public Class<AdminMove> packet() {
        return AdminMove.class;
    }
}
