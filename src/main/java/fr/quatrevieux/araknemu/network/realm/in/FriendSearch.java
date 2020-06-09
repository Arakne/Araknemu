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
 * Copyright (c) 2017-2020 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.network.realm.in;

import fr.quatrevieux.araknemu.core.network.parser.Packet;
import fr.quatrevieux.araknemu.core.network.parser.SinglePacketParser;

/**
 * Packet for find a friend server
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Account.as#L80
 */
final public class FriendSearch implements Packet {
    final static public class Parser implements SinglePacketParser<FriendSearch> {
        @Override
        public FriendSearch parse(String input) {
            return new FriendSearch(input);
        }

        @Override
        public String code() {
            return "AF";
        }
    }

    final private String pseudo;

    public FriendSearch(String pseudo) {
        this.pseudo = pseudo;
    }

    /**
     * Get the search pseudo
     */
    public String pseudo() {
        return pseudo;
    }
}
