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
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.network.game.GameSession;

/**
 * Base type for packet handle that needs the player to be in a game session
 *
 * @param <P> The packet type
 * @see GameSession#player() will not be null
 */
public abstract class AbstractPlayingPacketHandler<P extends Packet> implements PacketHandler<GameSession, P> {
    @Override
    public final void handle(GameSession session, P packet) throws Exception {
        final GamePlayer player = session.player();

        if (player == null) {
            throw new CloseImmediately("A character must be selected to handle the packet " + packet.getClass().getSimpleName());
        }

        handle(session, player, packet);
    }

    /**
     * Handle the packet with a selected character
     *
     * @param session The current game session
     * @param player The selected character
     * @param packet packet to handle
     */
    protected abstract void handle(GameSession session, GamePlayer player, P packet) throws Exception;
}
