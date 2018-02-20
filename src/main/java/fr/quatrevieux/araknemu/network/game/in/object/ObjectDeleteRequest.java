package fr.quatrevieux.araknemu.network.game.in.object;

import fr.quatrevieux.araknemu.network.in.Packet;
import fr.quatrevieux.araknemu.network.in.ParsePacketException;
import fr.quatrevieux.araknemu.network.in.SinglePacketParser;
import org.apache.commons.lang3.StringUtils;

/**
 * Delete an object from inventory
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Items.as#L31
 */
final public class ObjectDeleteRequest implements Packet{
    final static public class Parser implements SinglePacketParser<ObjectDeleteRequest> {
        @Override
        public ObjectDeleteRequest parse(String input) throws ParsePacketException {
            String[] parts = StringUtils.split(input, "|", 2);

            if (parts.length != 2) {
                throw new ParsePacketException("Od" + input, "Needs 2 parts");
            }

            return new ObjectDeleteRequest(
                Integer.parseInt(parts[0]),
                Integer.parseUnsignedInt(parts[1])
            );
        }

        @Override
        public String code() {
            return "Od";
        }
    }

    final private int id;
    final private int quantity;

    public ObjectDeleteRequest(int id, int quantity) {
        this.id = id;
        this.quantity = quantity;
    }

    public int id() {
        return id;
    }

    public int quantity() {
        return quantity;
    }
}
