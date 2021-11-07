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
import fr.quatrevieux.araknemu.core.network.parser.ParsePacketException;
import fr.quatrevieux.araknemu.core.network.session.ConfigurableSession;

import java.util.function.Consumer;

/**
 * Middleware for parse the realm packets
 *
 * Handle the two first packets :
 *
 * Client                       Server
 * [new connection] ------>     [initialise session]
 *                  <------     HC{key}
 * //=== [Start login, procedural packet exchange] ===//
 * DofusVersion     ------>     [check version]
 * Credentials      ------>     [parse credentials]
 *                  <------     BadVersion [if do not corresponds]
 *                  <------     LoginError [on bad credentials]
 *                  <------     Pseudo
 *                  <------     Community
 *                  <------     HostList
 *                  <------     GMLevel
 *                  <------     Question
 * //=== [Login success, standard packet exchange protocol] ===//
 * AskServerList    ------>     [load characters by server]
 *                  <------     ServerList
 * SelectServer     ------>     [check for server and declare connection]
 * [close]          <------     SelectServerXX
 */
public final class RealmPacketParserMiddleware implements ConfigurableSession.ReceivePacketMiddleware {
    private final PacketParser[] loginPackets;
    private final PacketParser parser;

    private int packetCount = 0;

    /**
     * @param loginPackets The "indexed" packets (i.e. recognized by there received position)
     * @param parser The other packets (i.e. recognized by there header)
     */
    public RealmPacketParserMiddleware(PacketParser[] loginPackets, PacketParser parser) {
        this.loginPackets = loginPackets;
        this.parser = parser;
    }

    @Override
    public void handlePacket(Object packet, Consumer<Object> next) {
        if (packet instanceof String) {
            packet = parse(packet.toString());
        }

        next.accept(packet);
    }

    /**
     * Parse the received packet
     */
    private Packet parse(String input) throws ParsePacketException {
        if (packetCount >= loginPackets.length) {
            return parser.parse(input);
        }

        return loginPackets[packetCount++].parse(input);
    }
}
