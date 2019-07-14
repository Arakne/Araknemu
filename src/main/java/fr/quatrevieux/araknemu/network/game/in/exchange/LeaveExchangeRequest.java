package fr.quatrevieux.araknemu.network.game.in.exchange;

import fr.quatrevieux.araknemu.network.in.Packet;
import fr.quatrevieux.araknemu.network.in.ParsePacketException;
import fr.quatrevieux.araknemu.network.in.SinglePacketParser;

/**
 * Leave the current exchange dialog or request
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Exchange.as#L19
 */
final public class LeaveExchangeRequest implements Packet {
    final static public class Parser implements SinglePacketParser<LeaveExchangeRequest> {
        @Override
        public LeaveExchangeRequest parse(String input) throws ParsePacketException {
            return new LeaveExchangeRequest();
        }

        @Override
        public String code() {
            return "EV";
        }
    }
}
