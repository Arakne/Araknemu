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

import fr.quatrevieux.araknemu.core.network.exception.ErrorPacket;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.game.out.info.Error;
import fr.quatrevieux.araknemu.core.network.parser.Packet;
import fr.quatrevieux.araknemu.core.network.parser.PacketHandler;

/**
 * Ensure that the session contains is not in fight, or the fight is not active
 *
 * @param <P> Packet to handler
 */
final public class EnsureInactiveFight<P extends Packet> implements PacketHandler<GameSession, P> {
    final private PacketHandler<GameSession, P> handler;

    public EnsureInactiveFight(PacketHandler<GameSession, P> handler) {
        this.handler = handler;
    }

    @Override
    public void handle(GameSession session, P packet) throws Exception {
        if (session.fighter() != null && session.fighter().fight() != null && session.fighter().fight().active()) {
            throw new ErrorPacket(Error.cantDoDuringFight());
        }

        handler.handle(session, packet);
    }

    @Override
    public Class<P> packet() {
        return handler.packet();
    }
}
