package fr.quatrevieux.araknemu.network.game.in.account;

import fr.quatrevieux.araknemu.network.in.Packet;
import fr.quatrevieux.araknemu.network.in.ParsePacketException;
import fr.quatrevieux.araknemu.network.in.SinglePacketParser;

/**
 * Packet for log into Game server
 */
final public class LoginToken implements Packet {
    final static public class Parser implements SinglePacketParser<LoginToken> {
        @Override
        public LoginToken parse(String input) throws ParsePacketException {
            return new LoginToken(input);
        }

        @Override
        public String code() {
            return "AT";
        }
    }

    final private String token;

    public LoginToken(String token) {
        this.token = token;
    }

    /**
     * Get the requested token value
     */
    public String token() {
        return token;
    }
}
