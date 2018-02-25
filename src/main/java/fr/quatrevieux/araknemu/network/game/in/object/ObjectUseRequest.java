package fr.quatrevieux.araknemu.network.game.in.object;

import fr.quatrevieux.araknemu.network.in.Packet;
import fr.quatrevieux.araknemu.network.in.ParsePacketException;
import fr.quatrevieux.araknemu.network.in.SinglePacketParser;
import org.apache.commons.lang3.StringUtils;

/**
 * Request for use an object
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Items.as#L35
 */
final public class ObjectUseRequest implements Packet {
    final static public class Parser implements SinglePacketParser<ObjectUseRequest> {
        @Override
        public ObjectUseRequest parse(String input) throws ParsePacketException {
            String[] parts = StringUtils.splitByWholeSeparatorPreserveAllTokens(input, "|", 3);

            if (parts.length == 3) {
                return new ObjectUseRequest(
                    Integer.parseInt(parts[0]),
                    parts[1].isEmpty() ? 0  : Integer.parseInt(parts[1]),
                    parts[2].isEmpty() ? -1 : Integer.parseInt(parts[2]),
                    true
                );
            }

            return new ObjectUseRequest(Integer.parseInt(parts[0]), 0, 0, false);
        }

        @Override
        public String code() {
            return "OU";
        }
    }

    final private int objectId;
    final private int target;
    final private int cell;
    final private boolean isTarget;

    public ObjectUseRequest(int objectId, int target, int cell, boolean isTarget) {
        this.objectId = objectId;
        this.target = target;
        this.cell = cell;
        this.isTarget = isTarget;
    }

    public int objectId() {
        return objectId;
    }

    public int target() {
        return target;
    }

    public int cell() {
        return cell;
    }

    public boolean isTarget() {
        return isTarget;
    }
}
