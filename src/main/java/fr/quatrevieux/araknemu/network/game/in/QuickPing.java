package fr.quatrevieux.araknemu.network.game.in;

import fr.quatrevieux.araknemu.network.in.Packet;
import fr.quatrevieux.araknemu.network.in.ParsePacketException;
import fr.quatrevieux.araknemu.network.in.SinglePacketParser;

/**
 * QuickPing request
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Aks.as#L342
 */
final public class QuickPing implements Packet {
    final static public class Parser implements SinglePacketParser<QuickPing> {
        @Override
        public QuickPing parse(String input) throws ParsePacketException {
            return new QuickPing();
        }

        @Override
        public String code() {
            return "qping";
        }
    }
}
