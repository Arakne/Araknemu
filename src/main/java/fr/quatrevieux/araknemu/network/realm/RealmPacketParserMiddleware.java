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

package fr.quatrevieux.araknemu.network.realm;

import fr.quatrevieux.araknemu.core.network.parser.Packet;
import fr.quatrevieux.araknemu.core.network.parser.PacketParser;
import fr.quatrevieux.araknemu.core.network.session.ConfigurableSession;
import fr.quatrevieux.araknemu.network.realm.in.RealmPacketParser;

import java.util.function.Consumer;

/**
 * Middleware for parse the realm packets
 */
final public class RealmPacketParserMiddleware implements ConfigurableSession.ReceivePacketMiddleware {
    final private RealmPacketParser parser;

    public RealmPacketParserMiddleware(PacketParser[] loginPackets, PacketParser baseParser) {
        parser = new RealmPacketParser(loginPackets, baseParser);
    }

    @Override
    public void handlePacket(Object packet, Consumer<Object> next) {
        if (!(packet instanceof Packet)) {
            packet = parser.parse(packet.toString());
        }

        next.accept(packet);
    }
}
