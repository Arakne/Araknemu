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
 * Copyright (c) 2017-2024 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.handler;

import fr.quatrevieux.araknemu.core.network.exception.CloseImmediately;
import fr.quatrevieux.araknemu.core.network.parser.Packet;
import fr.quatrevieux.araknemu.core.network.parser.PacketHandler;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.network.game.GameSession;

/**
 * Base handler type for packets requiring an exploration session
 *
 * @param <P> Packet to handle
 * @see GameSession#exploration() will be not null
 */
public abstract class AbstractExploringPacketHandler<P extends Packet> implements PacketHandler<GameSession, P> {
    @Override
    public final void handle(GameSession session, P packet) throws Exception {
        final ExplorationPlayer exploration = session.exploration();

        if (exploration == null) {
            throw new CloseImmediately("An exploration session is required to handle the packet " + packet.getClass().getSimpleName());
        }

        handle(session, exploration, packet);
    }

    /**
     * Handle the packet with the exploration session
     *
     * @param session The game session
     * @param exploration The exploration session
     * @param packet The packet to handle
     */
    protected abstract void handle(GameSession session, ExplorationPlayer exploration, P packet) throws Exception;
}
