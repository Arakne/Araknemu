package fr.quatrevieux.araknemu.network.game.in.game.action;

import fr.quatrevieux.araknemu.game.exploration.interaction.action.ActionType;
import fr.quatrevieux.araknemu.network.in.Packet;
import fr.quatrevieux.araknemu.network.in.ParsePacketException;
import fr.quatrevieux.araknemu.network.in.SinglePacketParser;
import org.apache.commons.lang3.StringUtils;

/**
 * Request for start a game action
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/GameActions.as#L19
 */
final public class GameActionRequest implements Packet {
    final static public class Parser implements SinglePacketParser<GameActionRequest> {
        @Override
        public GameActionRequest parse(String input) throws ParsePacketException {
            return new GameActionRequest(
                ActionType.byId(Integer.parseInt(input.substring(0, 3))),
                StringUtils.split(input.substring(3), ";")
            );
        }

        @Override
        public String code() {
            return "GA";
        }
    }

    final private ActionType type;
    final private String[] arguments;

    public GameActionRequest(ActionType type, String[] arguments) {
        this.type = type;
        this.arguments = arguments;
    }

    public ActionType type() {
        return type;
    }

    public String[] arguments() {
        return arguments;
    }
}
