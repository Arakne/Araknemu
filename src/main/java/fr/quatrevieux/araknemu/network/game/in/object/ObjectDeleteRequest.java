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
import org.checkerframework.checker.index.qual.Positive;
import org.checkerframework.common.value.qual.MinLen;

/**
 * Delete an object from inventory
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Items.as#L31
 */
public final class ObjectDeleteRequest implements Packet {
    private final int id;
    private final @Positive int quantity;

    public ObjectDeleteRequest(int id, @Positive int quantity) {
        this.id = id;
        this.quantity = quantity;
    }

    public int id() {
        return id;
    }

    public @Positive int quantity() {
        return quantity;
    }

    public static final class Parser implements SinglePacketParser<ObjectDeleteRequest> {
        @Override
        public ObjectDeleteRequest parse(String input) throws ParsePacketException {
            final PacketTokenizer tokenizer = tokenize(input, '|');

            return new ObjectDeleteRequest(
                tokenizer.nextInt(),
                tokenizer.nextPositiveInt()
            );
        }

        @Override
        public @MinLen(2) String code() {
            return "Od";
        }
    }
}
