package fr.quatrevieux.araknemu.network.game.in.emote;

import fr.quatrevieux.araknemu.game.world.map.Direction;
import fr.quatrevieux.araknemu.network.in.Packet;
import fr.quatrevieux.araknemu.network.in.ParsePacketException;
import fr.quatrevieux.araknemu.network.in.SinglePacketParser;

/**
 * Change the player sprite orientation on exploration
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Emotes.as#L32
 */
final public class SetOrientationRequest implements Packet {
    final static public class Parser implements SinglePacketParser<SetOrientationRequest> {
        @Override
        public SetOrientationRequest parse(String input) throws ParsePacketException {
            int number = input.charAt(0) - '0';

            if (number >= Direction.values().length) {
                throw new ParsePacketException(code() + input, "Invalid direction");
            }

            return new SetOrientationRequest(Direction.values()[number]);
        }

        @Override
        public String code() {
            return "eD";
        }
    }

    final private Direction orientation;

    public SetOrientationRequest(Direction orientation) {
        this.orientation = orientation;
    }

    public Direction orientation() {
        return orientation;
    }
}
