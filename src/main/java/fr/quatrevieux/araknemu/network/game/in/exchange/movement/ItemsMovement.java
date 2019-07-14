package fr.quatrevieux.araknemu.network.game.in.exchange.movement;

import fr.quatrevieux.araknemu.network.in.Packet;
import fr.quatrevieux.araknemu.network.in.ParsePacketException;
import fr.quatrevieux.araknemu.network.in.SinglePacketParser;
import org.apache.commons.lang3.StringUtils;

/**
 * Add or remove item from exchange
 *
 * @todo multiple items
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Exchange.as#L62
 */
final public class ItemsMovement implements Packet {
    final static public class Parser implements SinglePacketParser<ItemsMovement> {
        @Override
        public ItemsMovement parse(String input) throws ParsePacketException {
            final int sign = input.charAt(0) == '-' ? -1 : +1;
            final String[] parts = StringUtils.split(input.substring(1), "|", 3);

            if (parts.length < 2) {
                throw new ParsePacketException(code() + input, "Expects at least 2 parts");
            }

            return new ItemsMovement(
                Integer.parseInt(parts[0]),
                sign * Integer.parseInt(parts[1]),
                parts.length > 2 ? Integer.parseInt(parts[2]) : 0
            );
        }

        @Override
        public String code() {
            return "EMO";
        }
    }

    final private int id;
    final private int quantity;
    final private int price;

    public ItemsMovement(int id, int quantity, int price) {
        this.id = id;
        this.quantity = quantity;
        this.price = price;
    }

    public int id() {
        return id;
    }

    public int quantity() {
        return quantity;
    }

    public int price() {
        return price;
    }
}
