package fr.quatrevieux.araknemu.network.game.in.game.action;

import fr.quatrevieux.araknemu.network.in.Packet;
import fr.quatrevieux.araknemu.network.in.ParsePacketException;
import fr.quatrevieux.araknemu.network.in.SinglePacketParser;

/**
 * The game action is successfully executed by the client
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/GameActions.as#L24
 */
final public class GameActionAcknowledge implements Packet {
    final static public class Parser implements SinglePacketParser<GameActionAcknowledge> {
        @Override
        public GameActionAcknowledge parse(String input) throws ParsePacketException {
            return new GameActionAcknowledge(
                Integer.parseInt(input)
            );
        }

        @Override
        public String code() {
            return "GKK";
        }
    }

    final private int actionId;

    public GameActionAcknowledge(int actionId) {
        this.actionId = actionId;
    }

    public int actionId() {
        return actionId;
    }
}
