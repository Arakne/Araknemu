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

package fr.quatrevieux.araknemu.network.game.in.exchange.movement;

import fr.quatrevieux.araknemu.core.network.parser.Packet;
import fr.quatrevieux.araknemu.core.network.parser.PacketTokenizer;
import fr.quatrevieux.araknemu.core.network.parser.ParsePacketException;
import fr.quatrevieux.araknemu.core.network.parser.SinglePacketParser;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.common.value.qual.MinLen;

/**
 * Add or remove item from exchange
 *
 * @todo multiple items
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Exchange.as#L62
 */
public final class ItemsMovement implements Packet {
    private final int id;
    private final int quantity;
    private final @NonNegative int price;

    public ItemsMovement(int id, int quantity, @NonNegative int price) {
        this.id = id;
        this.quantity = quantity;
        this.price = price;
    }

    public int id() {
        return id;
    }

    public int quantity() {
        return quantity;
    }

    public int price() {
        return price;
    }

    public static final class Parser implements SinglePacketParser<ItemsMovement> {
        @Override
        public ItemsMovement parse(String input) throws ParsePacketException {
            if (input.isEmpty()) {
                throw new ParsePacketException(code(), "Missing packet arguments");
            }

            final int sign = input.charAt(0) == '-' ? -1 : +1;
            final PacketTokenizer tokenizer = new PacketTokenizer(code(), input.substring(1), '|');

            return new ItemsMovement(
                tokenizer.nextInt(),
                sign * tokenizer.nextPositiveInt(),
                tokenizer.nextNonNegativeIntOrDefault(0)
            );
        }

        @Override
        public @MinLen(2) String code() {
            return "EMO";
        }
    }
}
