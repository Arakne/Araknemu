package fr.quatrevieux.araknemu.network.game.in.exchange.movement;

import fr.quatrevieux.araknemu.network.in.Packet;
import fr.quatrevieux.araknemu.network.in.ParsePacketException;
import fr.quatrevieux.araknemu.network.in.SinglePacketParser;

/**
 * Change kamas on an exchange
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Exchange.as#L62
 */
final public class KamasMovement implements Packet {
    final static public class Parser implements SinglePacketParser<KamasMovement> {
        @Override
        public KamasMovement parse(String input) throws ParsePacketException {
            return new KamasMovement(Long.parseLong(input));
        }

        @Override
        public String code() {
            return "EMG";
        }
    }

    final private long quantity;

    public KamasMovement(long quantity) {
        this.quantity = quantity;
    }

    /**
     * Quantity of kamas to change
     * May be negative for remove kamas
     */
    public long quantity() {
        return quantity;
    }
}
