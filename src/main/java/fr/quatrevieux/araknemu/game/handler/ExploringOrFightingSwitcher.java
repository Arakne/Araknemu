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
 * Switch between handler when the current player is in fight or exploring
 */
final public class ExploringOrFightingSwitcher<P extends Packet> implements PacketHandler<GameSession, P> {
    final private PacketHandler<GameSession, P> exploringHandler;
    final private PacketHandler<GameSession, P> fightingHandler;

    /**
     * ExploringOrFightingSwitcher constructor
     *
     * @param exploringHandler The handler to use when client is in exploration
     * @param fightingHandler The handler to use when client is in exploration
     */
    public ExploringOrFightingSwitcher(PacketHandler<GameSession, P> exploringHandler, PacketHandler<GameSession, P> fightingHandler) {
        this.exploringHandler = exploringHandler;
        this.fightingHandler = fightingHandler;
    }

    @Override
    public void handle(GameSession session, P packet) throws Exception {
        if (session.fighter() != null) {
            fightingHandler.handle(session, packet);
        } else if (session.exploration() != null) {
            exploringHandler.handle(session, packet);
        } else {
            throw new CloseImmediately("The player should be in exploration or fight");
        }
    }

    @Override
    public Class<P> packet() {
        return exploringHandler.packet();
    }
}
