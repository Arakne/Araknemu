package fr.quatrevieux.araknemu.network.game.in.exchange;

import fr.quatrevieux.araknemu.network.in.Packet;
import fr.quatrevieux.araknemu.network.in.ParsePacketException;
import fr.quatrevieux.araknemu.network.in.SinglePacketParser;

/**
 * The player is ready for the exchange
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Exchange.as#L35
 */
final public class ExchangeReady implements Packet {
    final static public class Parser implements SinglePacketParser<ExchangeReady> {
        @Override
        public ExchangeReady parse(String input) throws ParsePacketException {
            return new ExchangeReady();
        }

        @Override
        public String code() {
            return "EK";
        }
    }
}
