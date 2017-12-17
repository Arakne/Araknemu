package fr.quatrevieux.araknemu.network.game.in.account;

import fr.quatrevieux.araknemu.network.in.Packet;
import fr.quatrevieux.araknemu.network.in.ParsePacketException;
import fr.quatrevieux.araknemu.network.in.SinglePacketParser;

/**
 * Ask for account gifts
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Account.as#L128
 */
final public class AskGift implements Packet {
    final static public class Parser implements SinglePacketParser<AskGift> {
        @Override
        public AskGift parse(String input) throws ParsePacketException {
            return new AskGift(input);
        }

        @Override
        public String code() {
            return "Ag";
        }
    }

    final private String language;

    public AskGift(String language) {
        this.language = language;
    }

    public String language() {
        return language;
    }
}
