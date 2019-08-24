package fr.quatrevieux.araknemu.network.game.in.exchange.store;

import fr.quatrevieux.araknemu.network.in.Packet;
import fr.quatrevieux.araknemu.network.in.ParsePacketException;
import fr.quatrevieux.araknemu.network.in.SinglePacketParser;
import org.apache.commons.lang3.StringUtils;

/**
 * Sell an item to an NPC
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Exchange.as#L70
 */
final public class SellRequest implements Packet {
    final static public class Parser implements SinglePacketParser<SellRequest> {
        @Override
        public SellRequest parse(String input) throws ParsePacketException {
            final String[] parts = StringUtils.split(input, "|", 2);

            if (parts.length != 2) {
                throw new ParsePacketException(code() + input, "Invalid parts number");
            }

            return new SellRequest(
                Integer.parseInt(parts[0]),
                Integer.parseUnsignedInt(parts[1])
            );
        }

        @Override
        public String code() {
            return "ES";
        }
    }

    final private int itemId;
    final private int quantity;

    public SellRequest(int itemId, int quantity) {
        this.itemId = itemId;
        this.quantity = quantity;
    }

    /**
     * The item entry id to sell
     */
    public int itemId() {
        return itemId;
    }

    /**
     * Quantity to sell
     */
    public int quantity() {
        return quantity;
    }
}
