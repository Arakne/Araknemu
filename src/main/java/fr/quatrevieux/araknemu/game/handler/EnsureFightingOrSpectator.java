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
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.network.game.GameSession;

/**
 * Ensure that the session contains a valid fighter or spectator, and execute the corresponding packet handler
 *
 * @param <P> Packet to handle
 */
public final class EnsureFightingOrSpectator<P extends Packet> implements PacketHandler<GameSession, P> {
    private final PacketHandler<GameSession, P> fighting;
    private final PacketHandler<GameSession, P> spectator;

    public EnsureFightingOrSpectator(PacketHandler<GameSession, P> fighting, PacketHandler<GameSession, P> spectator) {
        this.fighting = fighting;
        this.spectator = spectator;
    }

    @Override
    public void handle(GameSession session, P packet) {
        final Fight fight;

        if (session.fighter() != null) {
            fight = session.fighter().fight();
        } else if (session.spectator() != null) {
            fight = session.spectator().fight();
        } else {
            throw new CloseImmediately("Not in fight");
        }

        fight.execute(() -> {
            try {
                if (session.fighter() != null) {
                    fighting.handle(session, packet);
                } else if (session.spectator() != null) {
                    spectator.handle(session, packet);
                }
            } catch (Exception e) {
                session.exception(e);
            }
        });
    }

    @Override
    public Class<P> packet() {
        return fighting.packet();
    }
}
