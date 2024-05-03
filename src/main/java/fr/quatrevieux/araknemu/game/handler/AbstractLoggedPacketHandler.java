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
import fr.quatrevieux.araknemu.game.account.GameAccount;
import fr.quatrevieux.araknemu.network.game.GameSession;

/**
 * Base type for packet handle that needs the player to be successfully logged (i.e. has a linked account)
 *
 * @param <P> The packet type
 *
 * @see GameSession#isLogged() will be true
 * @see GameSession#account() will not be null
 */
public abstract class AbstractLoggedPacketHandler<P extends Packet> implements PacketHandler<GameSession, P> {
    @Override
    public final void handle(GameSession session, P packet) throws Exception {
        final GameAccount account = session.account();

        if (account == null) {
            throw new CloseImmediately("The player must be logged to handle the packet " + packet.getClass().getSimpleName());
        }

        handle(session, account, packet);
    }

    /**
     * Handle the packet when the account is successfully logged
     *
     * @param session The current game session
     * @param account The logged account
     * @param packet packet to handle
     */
    protected abstract void handle(GameSession session, GameAccount account, P packet) throws Exception;
}
