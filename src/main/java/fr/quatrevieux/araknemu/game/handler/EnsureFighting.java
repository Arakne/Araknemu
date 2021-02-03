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

package fr.quatrevieux.araknemu.game.handler;

import fr.quatrevieux.araknemu.core.network.exception.CloseImmediately;
import fr.quatrevieux.araknemu.core.network.parser.Packet;
import fr.quatrevieux.araknemu.core.network.parser.PacketHandler;
import fr.quatrevieux.araknemu.network.game.GameSession;

/**
 * Ensure that the session contains a valid fighter
 *
 * @param <P> Packet to handler
 */
public final class EnsureFighting<P extends Packet> implements PacketHandler<GameSession, P> {
    private final PacketHandler<GameSession, P> handler;

    public EnsureFighting(PacketHandler<GameSession, P> handler) {
        this.handler = handler;
    }

    @Override
    public void handle(GameSession session, P packet) {
        if (session.fighter() == null) {
            throw new CloseImmediately("Not in fight");
        }

        session.fighter().fight().execute(() -> {
            try {
                handler.handle(session, packet);
            } catch (Exception e) {
                session.exception(e);
            }
        });
    }

    @Override
    public Class<P> packet() {
        return handler.packet();
    }
}
