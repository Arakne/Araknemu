package fr.quatrevieux.araknemu.network.game.in.game.action;

import fr.quatrevieux.araknemu.network.in.Packet;
import fr.quatrevieux.araknemu.network.in.ParsePacketException;
import fr.quatrevieux.araknemu.network.in.SinglePacketParser;
import org.apache.commons.lang3.StringUtils;

/**
 * Cancel the current game action, and all its followers
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/GameActions.as#L28
 */
final public class GameActionCancel implements Packet {
    final static public class Parser implements SinglePacketParser<GameActionCancel> {
        @Override
        public GameActionCancel parse(String input) throws ParsePacketException {
            String[] parts = StringUtils.split(input, "|", 2);

            if (parts.length != 2) {
                throw new ParsePacketException("GKE" + input, "The packet should have 2 parts separated by a pipe");
            }

            return new GameActionCancel(
                Integer.parseInt(parts[0]),
                parts[1]
            );
        }

        @Override
        public String code() {
            return "GKE";
        }
    }

    final private int actionId;
    final private String argument;

    public GameActionCancel(int actionId, String argument) {
        this.actionId = actionId;
        this.argument = argument;
    }

    public int actionId() {
        return actionId;
    }

    public String argument() {
        return argument;
    }
}
