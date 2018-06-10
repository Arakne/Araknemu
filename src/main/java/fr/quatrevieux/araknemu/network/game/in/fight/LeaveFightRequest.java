package fr.quatrevieux.araknemu.network.game.in.fight;

import fr.quatrevieux.araknemu.network.in.Packet;
import fr.quatrevieux.araknemu.network.in.ParsePacketException;
import fr.quatrevieux.araknemu.network.in.SinglePacketParser;

/**
 * Leave the current fight
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Game.as#L27
 */
final public class LeaveFightRequest implements Packet {
    final static public class Parser implements SinglePacketParser<LeaveFightRequest> {
        @Override
        public LeaveFightRequest parse(String input) throws ParsePacketException {
            return new LeaveFightRequest();
        }

        @Override
        public String code() {
            return "GQ";
        }
    }
}
