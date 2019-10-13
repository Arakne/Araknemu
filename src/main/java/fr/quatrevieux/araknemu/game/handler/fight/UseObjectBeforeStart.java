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

package fr.quatrevieux.araknemu.game.handler.fight;

import fr.quatrevieux.araknemu.game.item.type.UsableItem;
import fr.quatrevieux.araknemu.game.player.inventory.InventoryEntry;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.game.in.object.ObjectUseRequest;
import fr.quatrevieux.araknemu.network.game.out.basic.Noop;
import fr.quatrevieux.araknemu.core.network.parser.PacketHandler;

/**
 * Use an object before start the fight
 */
final public class UseObjectBeforeStart implements PacketHandler<GameSession, ObjectUseRequest> {
    @Override
    public void handle(GameSession session, ObjectUseRequest packet) {
        InventoryEntry entry = session.player().inventory().get(packet.objectId());

        UsableItem item = UsableItem.class.cast(entry.item());

        if (!item.checkFighter(session.fighter())) {
            session.write(new Noop());
            return;
        }

        try {
            item.applyToFighter(session.fighter());
        } finally {
            entry.remove(1);
        }
    }

    @Override
    public Class<ObjectUseRequest> packet() {
        return ObjectUseRequest.class;
    }
}
