package fr.quatrevieux.araknemu.network.game.in.account;

import fr.quatrevieux.araknemu.network.in.Packet;
import fr.quatrevieux.araknemu.network.in.ParsePacketException;
import fr.quatrevieux.araknemu.network.in.SinglePacketParser;

/**
 * Packet for identify client (the client UID will be the same over all sessions)
 *
 * This packet will be send only once during runtime of the dofus client, AND only if a server is selected
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Account.as#L177
 */
final public class ClientUid implements Packet {
    final static public class Parser implements SinglePacketParser<ClientUid> {
        @Override
        public ClientUid parse(String input) throws ParsePacketException {
            return new ClientUid(input);
        }

        @Override
        public String code() {
            return "Ai";
        }
    }

    final private String uid;

    public ClientUid(String uid) {
        this.uid = uid;
    }

    public String uid() {
        return uid;
    }
}
