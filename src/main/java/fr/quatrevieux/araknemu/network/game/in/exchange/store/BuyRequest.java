package fr.quatrevieux.araknemu.network.game.in.exchange.store;

import fr.quatrevieux.araknemu.network.in.Packet;
import fr.quatrevieux.araknemu.network.in.ParsePacketException;
import fr.quatrevieux.araknemu.network.in.SinglePacketParser;
import org.apache.commons.lang3.StringUtils;

/**
 * Request for buy an item from a store (NPC or player)
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Exchange.as#L74
 */
final public class BuyRequest implements Packet {
    final static public class Parser implements SinglePacketParser<BuyRequest> {
        @Override
        public BuyRequest parse(String input) throws ParsePacketException {
            String[] parts = StringUtils.split(input, "|", 2);

            if (parts.length != 2) {
                throw new ParsePacketException(code() + input, "Invalid parts");
            }

            return new BuyRequest(
                Integer.parseInt(parts[0]),
                Integer.parseInt(parts[1])
            );
        }

        @Override
        public String code() {
            return "EB";
        }
    }

    final private int itemId;
    final private int quantity;

    public BuyRequest(int itemId, int quantity) {
        this.itemId = itemId;
        this.quantity = quantity;
    }

    /**
     * The item id
     *
     * Note: for an NPC, this is the item template id, whereas for player it's the item entry id
     */
    public int itemId() {
        return itemId;
    }

    /**
     * The buy quantity
     */
    public int quantity() {
        return quantity;
    }
}
