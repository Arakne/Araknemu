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

import fr.quatrevieux.araknemu.game.item.inventory.exception.InventoryException;
import fr.quatrevieux.araknemu.core.network.exception.ErrorPacket;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.game.in.object.ObjectDeleteRequest;
import fr.quatrevieux.araknemu.network.game.out.object.ItemDeletionError;
import fr.quatrevieux.araknemu.core.network.parser.PacketHandler;

/**
 * Handle object deletion
 */
final public class RemoveObject implements PacketHandler<GameSession, ObjectDeleteRequest> {
    @Override
    public void handle(GameSession session, ObjectDeleteRequest packet) throws Exception {
        try {
            session.player().inventory()
                .get(packet.id())
                .remove(packet.quantity())
            ;
        } catch (InventoryException e) {
            throw new ErrorPacket(new ItemDeletionError(), e);
        }
    }

    @Override
    public Class<ObjectDeleteRequest> packet() {
        return ObjectDeleteRequest.class;
    }
}
