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

import fr.quatrevieux.araknemu.core.network.parser.PacketHandler;
import fr.quatrevieux.araknemu.game.item.inventory.ItemEntry;
import fr.quatrevieux.araknemu.game.item.inventory.exception.AlreadyEquippedException;
import fr.quatrevieux.araknemu.game.item.inventory.exception.BadLevelException;
import fr.quatrevieux.araknemu.game.player.inventory.InventoryEntry;
import fr.quatrevieux.araknemu.game.player.inventory.PlayerInventory;
import fr.quatrevieux.araknemu.game.player.inventory.slot.InventorySlots;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.game.in.object.ObjectMoveRequest;
import fr.quatrevieux.araknemu.network.game.out.object.AddItemError;
import org.checkerframework.checker.nullness.util.NullnessUtil;
import org.checkerframework.common.value.qual.IntRange;

/**
 * Move an object from the repository
 */
public final class MoveObject implements PacketHandler<GameSession, ObjectMoveRequest> {
    @Override
    public void handle(GameSession session, ObjectMoveRequest packet) throws Exception {
        final PlayerInventory inventory = NullnessUtil.castNonNull(session.player()).inventory();
        final @IntRange(from = -1, to = InventorySlots.SLOT_MAX) int position = packet.position();

        if (position != ItemEntry.DEFAULT_POSITION) {
            // Unequip the item from the target position
            inventory.bySlot(position)
                .filter(entry -> entry.id() != packet.id())
                .ifPresent(InventoryEntry::unequip)
            ;
        }

        try {
            inventory
                .get(packet.id())
                .move(position, packet.quantity())
            ;
        } catch (BadLevelException e) {
            session.send(new AddItemError(AddItemError.Error.TOO_LOW_LEVEL));
        } catch (AlreadyEquippedException e) {
            session.send(new AddItemError(AddItemError.Error.ALREADY_EQUIPED));
        }
    }

    @Override
    public Class<ObjectMoveRequest> packet() {
        return ObjectMoveRequest.class;
    }
}
