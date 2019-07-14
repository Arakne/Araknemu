package fr.quatrevieux.araknemu.network.game.in.exchange;

import fr.quatrevieux.araknemu.network.in.Packet;
import fr.quatrevieux.araknemu.network.in.ParsePacketException;
import fr.quatrevieux.araknemu.network.in.SinglePacketParser;

/**
 * Accept the request and start the exchange
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Exchange.as#L31
 */
final public class AcceptExchangeRequest implements Packet {
    final static public class Parser implements SinglePacketParser<AcceptExchangeRequest> {
        @Override
        public AcceptExchangeRequest parse(String input) throws ParsePacketException {
            return new AcceptExchangeRequest();
        }

        @Override
        public String code() {
            return "EA";
        }
    }
}
