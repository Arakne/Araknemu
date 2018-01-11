package fr.quatrevieux.araknemu.network.game.in.basic;

import fr.quatrevieux.araknemu.network.in.Packet;
import fr.quatrevieux.araknemu.network.in.ParsePacketException;
import fr.quatrevieux.araknemu.network.in.SinglePacketParser;

/**
 * Ask for server date and time
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Basics.as#L51
 */
final public class AskDate implements Packet {
    final static public class Parser implements SinglePacketParser<AskDate> {
        @Override
        public AskDate parse(String input) throws ParsePacketException {
            return new AskDate();
        }

        @Override
        public String code() {
            return "BD";
        }
    }
}
