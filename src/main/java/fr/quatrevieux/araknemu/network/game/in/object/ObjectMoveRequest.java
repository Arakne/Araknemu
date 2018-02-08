package fr.quatrevieux.araknemu.network.game.in.object;

import fr.quatrevieux.araknemu.network.in.Packet;
import fr.quatrevieux.araknemu.network.in.ParsePacketException;
import fr.quatrevieux.araknemu.network.in.SinglePacketParser;
import org.apache.commons.lang3.StringUtils;

/**
 * Request for object move
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Items.as#L19
 */
final public class ObjectMoveRequest implements Packet {
    final static public class Parser implements SinglePacketParser<ObjectMoveRequest> {
        @Override
        public ObjectMoveRequest parse(String input) throws ParsePacketException {
            String[] parts = StringUtils.split(input, "|", 3);

            return new ObjectMoveRequest(
                Integer.parseInt(parts[0]),
                Integer.parseInt(parts[1]),
                parts.length == 3 ? Integer.parseUnsignedInt(parts[2]) : 1
            );
        }

        @Override
        public String code() {
            return "OM";
        }
    }

    final private int id;
    final private int position;
    final private int quantity;

    public ObjectMoveRequest(int id, int position, int quantity) {
        this.id = id;
        this.position = position;
        this.quantity = quantity;
    }

    public int id() {
        return id;
    }

    public int position() {
        return position;
    }

    public int quantity() {
        return quantity;
    }
}
