package fr.quatrevieux.araknemu.network.game.in.account;

import fr.quatrevieux.araknemu.network.in.Packet;
import fr.quatrevieux.araknemu.network.in.ParsePacketException;
import fr.quatrevieux.araknemu.network.in.SinglePacketParser;

/**
 * Ask for the character list
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Account.as#L63
 */
final public class AskCharacterList implements Packet {
    final static public class Parser implements SinglePacketParser<AskCharacterList> {
        @Override
        public AskCharacterList parse(String input) throws ParsePacketException {
            return new AskCharacterList(
                input.equals("f")
            );
        }

        @Override
        public String code() {
            return "AL";
        }
    }

    final private boolean forced;

    public AskCharacterList(boolean forced) {
        this.forced = forced;
    }

    /**
     * Force ask for character list
     */
    public boolean forced() {
        return forced;
    }
}
