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

package fr.quatrevieux.araknemu.network.realm.in;

import fr.quatrevieux.araknemu.core.network.parser.Packet;
import fr.quatrevieux.araknemu.core.network.parser.PacketParser;
import fr.quatrevieux.araknemu.core.network.parser.ParsePacketException;

/**
 * Packet parser for realm
 *
 * /!\ This parser MUST be create by RealmSession
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
final public class RealmPacketParser implements PacketParser {
    final private PacketParser[] loginPackets;
    final private PacketParser parser;

    private int packetCount = 0;

    public RealmPacketParser(PacketParser[] loginPackets, PacketParser parser) {
        this.loginPackets = loginPackets;
        this.parser = parser;
    }

    @Override
    public Packet parse(String input) throws ParsePacketException {
        if (packetCount >= loginPackets.length) {
            return parser.parse(input);
        }

        return loginPackets[packetCount++].parse(input);
    }
}
