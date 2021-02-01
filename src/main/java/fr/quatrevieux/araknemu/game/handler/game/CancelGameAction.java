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

package fr.quatrevieux.araknemu.game.handler.game;

import fr.quatrevieux.araknemu.core.network.parser.PacketHandler;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.game.in.game.action.GameActionCancel;

/**
 * Cancel the current game action
 */
public final class CancelGameAction implements PacketHandler<GameSession, GameActionCancel> {
    @Override
    public void handle(GameSession session, GameActionCancel packet) throws Exception {
        session
            .exploration()
            .interactions()
            .cancel(
                packet.actionId(),
                packet.argument()
            )
        ;
    }

    @Override
    public Class<GameActionCancel> packet() {
        return GameActionCancel.class;
    }
}
