package fr.quatrevieux.araknemu.network.realm.in;

import fr.quatrevieux.araknemu.network.in.Packet;
import fr.quatrevieux.araknemu.network.in.ParsePacketException;
import fr.quatrevieux.araknemu.network.in.SinglePacketParser;

/**
 * Choose a game server
 */
final public class ChooseServer implements Packet {
    final static public class Parser implements SinglePacketParser<ChooseServer> {
        @Override
        public ChooseServer parse(String input) throws ParsePacketException {
            try {
                return new ChooseServer(Integer.parseInt(input));
            } catch (NumberFormatException e) {
                throw new ParsePacketException(code(), "Invalid server number", e);
            }
        }

        @Override
        public String code() {
            return "AX";
        }
    }

    final private int id;

    public ChooseServer(int id) {
        this.id = id;
    }

    /**
     * The server id
     */
    public int id() {
        return id;
    }
}
