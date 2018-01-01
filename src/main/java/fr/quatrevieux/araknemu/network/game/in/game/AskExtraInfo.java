package fr.quatrevieux.araknemu.network.game.in.game;

import fr.quatrevieux.araknemu.network.in.Packet;
import fr.quatrevieux.araknemu.network.in.ParsePacketException;
import fr.quatrevieux.araknemu.network.in.SinglePacketParser;

/**
 * Packet for loading extra map information like sprites, items, fights...
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Game.as#L46
 */
final public class AskExtraInfo implements Packet {
    final static public class Parser implements SinglePacketParser<AskExtraInfo> {
        @Override
        public AskExtraInfo parse(String input) throws ParsePacketException {
            return new AskExtraInfo();
        }

        @Override
        public String code() {
            return "GI";
        }
    }
}
