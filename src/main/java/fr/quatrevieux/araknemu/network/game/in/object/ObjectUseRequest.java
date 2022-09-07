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
import org.checkerframework.checker.index.qual.GTENegativeOne;
import org.checkerframework.common.value.qual.MinLen;
import org.checkerframework.dataflow.qual.Pure;

/**
 * Request for use an object
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Items.as#L35
 */
public final class ObjectUseRequest implements Packet {
    private final int objectId;
    private final int target;
    private final @GTENegativeOne int cell;
    private final boolean isTarget;

    public ObjectUseRequest(int objectId, int target, @GTENegativeOne int cell, boolean isTarget) {
        this.objectId = objectId;
        this.target = target;
        this.cell = cell;
        this.isTarget = isTarget;
    }

    @Pure
    public int objectId() {
        return objectId;
    }

    @Pure
    public int target() {
        return target;
    }

    @Pure
    public @GTENegativeOne int cell() {
        return cell;
    }

    /**
     * Does a target is provided by the packet (i.e. use on target)
     */
    @Pure
    public boolean isTarget() {
        return isTarget;
    }

    public static final class Parser implements SinglePacketParser<ObjectUseRequest> {
        @Override
        public ObjectUseRequest parse(String input) throws ParsePacketException {
            final PacketTokenizer tokenizer = tokenize(input, '|');

            final int objectId = tokenizer.nextInt();

            if (tokenizer.hasNext()) {
                return new ObjectUseRequest(
                    objectId,
                    tokenizer.nextIntOrDefault(0),
                    tokenizer.nextNonNegativeOrNegativeOneIntOrDefault(-1),
                    true
                );
            }

            return new ObjectUseRequest(objectId, 0, 0, false);
        }

        @Override
        public @MinLen(2) String code() {
            return "OU";
        }
    }
}
