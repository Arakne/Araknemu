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
import fr.quatrevieux.araknemu.core.network.parser.ParsePacketException;
import fr.quatrevieux.araknemu.core.network.parser.SinglePacketParser;
import org.apache.commons.lang3.StringUtils;

/**
 * Request for object move
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Items.as#L19
 */
public final class ObjectMoveRequest implements Packet {
    private final int id;
    private final int position;
    private final int quantity;

    public ObjectMoveRequest(int id, int position, int quantity) {
        this.id = id;
        this.position = position;
        this.quantity = quantity;
    }

    public int id() {
        return id;
    }

    public int position() {
        return position;
    }

    public int quantity() {
        return quantity;
    }

    public static final class Parser implements SinglePacketParser<ObjectMoveRequest> {
        @Override
        public ObjectMoveRequest parse(String input) throws ParsePacketException {
            final String[] parts = StringUtils.split(input, "|", 3);

            return new ObjectMoveRequest(
                Integer.parseInt(parts[0]),
                Integer.parseInt(parts[1]),
                parts.length == 3 ? Integer.parseUnsignedInt(parts[2]) : 1
            );
        }

        @Override
        public String code() {
            return "OM";
        }
    }
}
