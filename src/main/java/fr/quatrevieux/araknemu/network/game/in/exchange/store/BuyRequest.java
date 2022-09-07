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

package fr.quatrevieux.araknemu.network.game.in.exchange.store;

import fr.quatrevieux.araknemu.core.network.parser.Packet;
import fr.quatrevieux.araknemu.core.network.parser.PacketTokenizer;
import fr.quatrevieux.araknemu.core.network.parser.ParsePacketException;
import fr.quatrevieux.araknemu.core.network.parser.SinglePacketParser;
import org.checkerframework.checker.index.qual.Positive;
import org.checkerframework.common.value.qual.MinLen;

/**
 * Request for buy an item from a store (NPC or player)
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Exchange.as#L74
 */
public final class BuyRequest implements Packet {
    private final int itemId;
    private final @Positive int quantity;

    public BuyRequest(int itemId, @Positive int quantity) {
        this.itemId = itemId;
        this.quantity = quantity;
    }

    /**
     * The item id
     *
     * Note: for an NPC, this is the item template id, whereas for player it's the item entry id
     */
    public int itemId() {
        return itemId;
    }

    /**
     * The buy quantity
     */
    public @Positive int quantity() {
        return quantity;
    }

    public static final class Parser implements SinglePacketParser<BuyRequest> {
        @Override
        public BuyRequest parse(String input) throws ParsePacketException {
            final PacketTokenizer tokenizer = tokenize(input, '|');

            return new BuyRequest(
                tokenizer.nextInt(),
                tokenizer.nextPositiveInt()
            );
        }

        @Override
        public @MinLen(2) String code() {
            return "EB";
        }
    }
}
