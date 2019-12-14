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
import fr.quatrevieux.araknemu.core.network.parser.ParsePacketException;
import fr.quatrevieux.araknemu.core.network.parser.SinglePacketParser;
import org.apache.commons.lang3.StringUtils;

/**
 * Request for buy an item from a store (NPC or player)
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Exchange.as#L74
 */
final public class BuyRequest implements Packet {
    final static public class Parser implements SinglePacketParser<BuyRequest> {
        @Override
        public BuyRequest parse(String input) throws ParsePacketException {
            String[] parts = StringUtils.split(input, "|", 2);

            if (parts.length != 2) {
                throw new ParsePacketException(code() + input, "Invalid parts");
            }

            return new BuyRequest(
                Integer.parseInt(parts[0]),
                Integer.parseInt(parts[1])
            );
        }

        @Override
        public String code() {
            return "EB";
        }
    }

    final private int itemId;
    final private int quantity;

    public BuyRequest(int itemId, int quantity) {
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
    public int quantity() {
        return quantity;
    }
}
