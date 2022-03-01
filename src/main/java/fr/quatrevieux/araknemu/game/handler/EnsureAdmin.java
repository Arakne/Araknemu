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
import org.checkerframework.checker.nullness.util.NullnessUtil;

/**
 * Wrap packet handler for ensure that as admin access
 *
 * @param <P> The packet to handle
 */
public final class EnsureAdmin<P extends Packet> implements PacketHandler<GameSession, P> {
    private final PacketHandler<GameSession, P> inner;

    public EnsureAdmin(PacketHandler<GameSession, P> inner) {
        this.inner = inner;
    }

    @Override
    public void handle(GameSession session, P packet) throws Exception {
        if (session.player() == null || !NullnessUtil.castNonNull(session.account()).isMaster()) {
            throw new CloseImmediately("Admin account required");
        }

        inner.handle(session, packet);
    }

    @Override
    public Class<P> packet() {
        return inner.packet();
    }
}
