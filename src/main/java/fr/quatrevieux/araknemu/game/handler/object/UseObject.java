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

import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.npc.GameNpc;
import fr.quatrevieux.araknemu.game.item.type.UsableItem;
import fr.quatrevieux.araknemu.game.player.inventory.InventoryEntry;
import fr.quatrevieux.araknemu.game.exploration.creature.Operation;
import fr.quatrevieux.araknemu.core.network.exception.ErrorPacket;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.game.in.object.ObjectUseRequest;
import fr.quatrevieux.araknemu.network.game.out.basic.Noop;
import fr.quatrevieux.araknemu.network.game.out.info.Error;
import fr.quatrevieux.araknemu.core.network.parser.PacketHandler;

/**
 * Use an object
 */
final public class UseObject implements PacketHandler<GameSession, ObjectUseRequest> {
    static private class ApplyItemOperation implements Operation {
        final private UsableItem item;
        final private ExplorationPlayer caster;
        final private int targetCell;

        private boolean success = false;

        public ApplyItemOperation(UsableItem item, ExplorationPlayer caster, int targetCell) {
            this.item = item;
            this.caster = caster;
            this.targetCell = targetCell;
        }

        @Override
        public void onExplorationPlayer(ExplorationPlayer target) {
            if (!item.checkTarget(caster, target, targetCell)) {
                success = false;
                return;
            }

            item.applyToTarget(caster, target, targetCell);
            success = true;
        }

        @Override
        public void onNpc(GameNpc npc) {
            onExplorationPlayer(null);
        }

        public void onNull() {
            onExplorationPlayer(null);
        }
    }

    @Override
    public void handle(GameSession session, ObjectUseRequest packet) throws Exception {
        if (!session.player().restrictions().canUseObject()) {
            throw new ErrorPacket(Error.cantDoOnCurrentState());
        }

        InventoryEntry entry = session.player().inventory().get(packet.objectId());

        UsableItem item = UsableItem.class.cast(entry.item());

        boolean result = true;
        try {
            result = packet.isTarget()
                ? handleForTarget(session, item, packet)
                : handleForSelf(session, item)
            ;
        } finally {
            if (result) {
                entry.remove(1);
            } else {
                session.send(new Noop());
            }
        }
    }

    @Override
    public Class<ObjectUseRequest> packet() {
        return ObjectUseRequest.class;
    }

    private boolean handleForSelf(GameSession session, UsableItem item) {
        if (!item.check(session.exploration())) {
            return false;
        }

        item.apply(session.exploration());
        return true;
    }

    private boolean handleForTarget(GameSession session, UsableItem item, ObjectUseRequest packet) {
        final ApplyItemOperation operation = new ApplyItemOperation(item, session.exploration(), packet.cell());
        final ExplorationMap map = session.exploration().map();

        if (map.has(packet.target())) {
            map.creature(packet.target()).apply(operation);
        } else {
            operation.onNull();
        }

        return operation.success;
    }
}
