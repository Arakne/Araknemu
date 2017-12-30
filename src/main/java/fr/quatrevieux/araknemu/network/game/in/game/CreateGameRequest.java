package fr.quatrevieux.araknemu.network.game.in.game;

import fr.quatrevieux.araknemu.network.in.Packet;
import fr.quatrevieux.araknemu.network.in.ParsePacketException;
import fr.quatrevieux.araknemu.network.in.SinglePacketParser;

/**
 * Ask for create new game session
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Game.as#L23
 */
final public class CreateGameRequest implements Packet {
    public enum Type {
        NONE,
        EXPLORATION,
        FIGHT
    }

    final static public class Parser implements SinglePacketParser<CreateGameRequest> {
        @Override
        public CreateGameRequest parse(String input) throws ParsePacketException {
            return new CreateGameRequest(
                Type.values()[input.charAt(0) - '0']
            );
        }

        @Override
        public String code() {
            return "GC";
        }
    }

    final private Type type;

    public CreateGameRequest(Type type) {
        this.type = type;
    }

    public Type type() {
        return type;
    }
}
