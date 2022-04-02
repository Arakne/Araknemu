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

package fr.quatrevieux.araknemu.network.game.in.account;

import fr.quatrevieux.araknemu.core.network.parser.Packet;
import fr.quatrevieux.araknemu.core.network.parser.ParsePacketException;
import fr.quatrevieux.araknemu.core.network.parser.SinglePacketParser;
import org.checkerframework.common.value.qual.MinLen;

/**
 * Packet for identify client (the client UID will be the same over all sessions)
 *
 * This packet will be send only once during runtime of the dofus client, AND only if a server is selected
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Account.as#L177
 */
public final class ClientUid implements Packet {
    private final String uid;

    public ClientUid(String uid) {
        this.uid = uid;
    }

    public String uid() {
        return uid;
    }

    public static final class Parser implements SinglePacketParser<ClientUid> {
        @Override
        public ClientUid parse(String input) throws ParsePacketException {
            return new ClientUid(input);
        }

        @Override
        public @MinLen(2) String code() {
            return "Ai";
        }
    }
}
