package fr.quatrevieux.araknemu.network.game.in.fight;

import fr.quatrevieux.araknemu.network.in.Packet;
import fr.quatrevieux.araknemu.network.in.ParsePacketException;
import fr.quatrevieux.araknemu.network.in.SinglePacketParser;

/**
 * End the current turn
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Game.as#L50
 */
final public class TurnEnd implements Packet {
    final static public class Parser implements SinglePacketParser<TurnEnd> {
        @Override
        public TurnEnd parse(String input) throws ParsePacketException {
            return new TurnEnd();
        }

        @Override
        public String code() {
            return "Gt";
        }
    }
}
