package fr.quatrevieux.araknemu.network.game.in.account;

import fr.quatrevieux.araknemu.network.in.Packet;
import fr.quatrevieux.araknemu.network.in.ParsePacketException;
import fr.quatrevieux.araknemu.network.in.SinglePacketParser;

/**
 * Ask for character name generation
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Account.as#L141
 */
final public class AskRandomName implements Packet {
    final static public class Parser implements SinglePacketParser<AskRandomName> {
        @Override
        public AskRandomName parse(String input) throws ParsePacketException {
            return new AskRandomName();
        }

        @Override
        public String code() {
            return "AP";
        }
    }
}
