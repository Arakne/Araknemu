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

package fr.quatrevieux.araknemu.network.game.in.object;

import fr.quatrevieux.araknemu.core.network.parser.Packet;
import fr.quatrevieux.araknemu.core.network.parser.PacketTokenizer;
import fr.quatrevieux.araknemu.core.network.parser.ParsePacketException;
import fr.quatrevieux.araknemu.core.network.parser.SinglePacketParser;
import fr.quatrevieux.araknemu.game.player.inventory.slot.InventorySlots;
import org.checkerframework.checker.index.qual.GTENegativeOne;
import org.checkerframework.checker.index.qual.Positive;
import org.checkerframework.common.value.qual.IntRange;
import org.checkerframework.common.value.qual.MinLen;

/**
 * Request for object move
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Items.as#L19
 */
public final class ObjectMoveRequest implements Packet {
    private final int id;
    private final @IntRange(from = -1, to = InventorySlots.SLOT_MAX) int position;
    private final @Positive int quantity;

    public ObjectMoveRequest(int id, @IntRange(from = -1, to = InventorySlots.SLOT_MAX) int position, @Positive int quantity) {
        this.id = id;
        this.position = position;
        this.quantity = quantity;
    }

    public int id() {
        return id;
    }

    public @IntRange(from = -1, to = InventorySlots.SLOT_MAX) int position() {
        return position;
    }

    public @Positive int quantity() {
        return quantity;
    }

    public static final class Parser implements SinglePacketParser<ObjectMoveRequest> {
        @Override
        public ObjectMoveRequest parse(String input) throws ParsePacketException {
            final PacketTokenizer tokenizer = tokenize(input, '|');

            return new ObjectMoveRequest(
                tokenizer.nextInt(),
                checkValidPosition(tokenizer.nextNonNegativeOrNegativeOneInt()),
                tokenizer.nextPositiveIntOrDefault(1)
            );
        }

        @Override
        public @MinLen(2) String code() {
            return "OM";
        }

        private @IntRange(from = -1, to = InventorySlots.SLOT_MAX) int checkValidPosition(@GTENegativeOne int position) {
            if (position > InventorySlots.SLOT_MAX) {
                throw new ParsePacketException(code(), "Invalid object position " + position);
            }

            return position;
        }
    }
}
