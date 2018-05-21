package fr.quatrevieux.araknemu.network.game.in.fight;

import fr.quatrevieux.araknemu.network.in.Packet;
import fr.quatrevieux.araknemu.network.in.ParsePacketException;
import fr.quatrevieux.araknemu.network.in.SinglePacketParser;

/**
 * Ask for details on the fight
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Fights.as#L23
 */
final public class AskFightDetails implements Packet {
    final static public class Parser implements SinglePacketParser<AskFightDetails> {
        @Override
        public AskFightDetails parse(String input) throws ParsePacketException {
            return new AskFightDetails(Integer.parseInt(input));
        }

        @Override
        public String code() {
            return "fD";
        }
    }


    final private int fightId;

    public AskFightDetails(int fightId) {
        this.fightId = fightId;
    }

    public int fightId() {
        return fightId;
    }
}
