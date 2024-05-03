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

package fr.quatrevieux.araknemu.game.handler.emote;

import fr.quatrevieux.araknemu.core.network.exception.ErrorPacket;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.handler.AbstractExploringPacketHandler;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.game.in.emote.SetOrientationRequest;
import fr.quatrevieux.araknemu.network.game.out.basic.Noop;

/**
 * Change the exploration player orientation for role player
 *
 * @see SetOrientationRequest
 */
public final class ChangeOrientation extends AbstractExploringPacketHandler<SetOrientationRequest> {
    @Override
    public void handle(GameSession session, ExplorationPlayer exploration, SetOrientationRequest packet) throws Exception {
        if (!exploration.player().restrictions().canMoveAllDirections() && !packet.orientation().restricted()) {
            throw new ErrorPacket(new Noop());
        }

        exploration.setOrientation(packet.orientation());
    }

    @Override
    public Class<SetOrientationRequest> packet() {
        return SetOrientationRequest.class;
    }
}
