package fr.quatrevieux.araknemu.network.realm.in;

import fr.quatrevieux.araknemu.network.in.Packet;
import fr.quatrevieux.araknemu.network.in.PacketParser;
import fr.quatrevieux.araknemu.network.in.ParsePacketException;

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
