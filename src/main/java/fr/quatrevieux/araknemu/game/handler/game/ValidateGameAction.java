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

import fr.quatrevieux.araknemu.core.network.exception.ErrorPacket;
import fr.quatrevieux.araknemu.core.network.parser.PacketHandler;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ActionFactory;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ActionType;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.game.in.game.action.GameActionRequest;
import fr.quatrevieux.araknemu.network.game.out.game.action.GameActionResponse;

/**
 * Validate and start a game action
 */
final public class ValidateGameAction implements PacketHandler<GameSession, GameActionRequest> {
    final private ActionFactory factory;

    public ValidateGameAction(ActionFactory factory) {
        this.factory = factory;
    }

    @Override
    public void handle(GameSession session, GameActionRequest packet) throws Exception {
        try {
            session.exploration().interactions().push(
                factory.create(
                    session.exploration(),
                    ActionType.byId(packet.type()),
                    packet.arguments()
                )
            );
        } catch (Exception e) {
            throw new ErrorPacket(GameActionResponse.NOOP, e);
        }
    }

    @Override
    public Class<GameActionRequest> packet() {
        return GameActionRequest.class;
    }
}
