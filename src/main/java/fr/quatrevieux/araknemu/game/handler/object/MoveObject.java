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

package fr.quatrevieux.araknemu.game.handler.object;

import fr.quatrevieux.araknemu.game.item.inventory.exception.BadLevelException;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.game.in.object.ObjectMoveRequest;
import fr.quatrevieux.araknemu.network.game.out.object.AddItemError;
import fr.quatrevieux.araknemu.core.network.parser.PacketHandler;

/**
 * Move an object from the repository
 */
final public class MoveObject implements PacketHandler<GameSession, ObjectMoveRequest> {
    @Override
    public void handle(GameSession session, ObjectMoveRequest packet) throws Exception {
        try {
            session.player()
                .inventory()
                .get(packet.id())
                .move(
                    packet.position(),
                    packet.quantity()
                )
            ;
        } catch (BadLevelException e) {
            session.send(new AddItemError(AddItemError.Error.TOO_LOW_LEVEL));
        }
    }

    @Override
    public Class<ObjectMoveRequest> packet() {
        return ObjectMoveRequest.class;
    }
}
