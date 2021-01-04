package fr.quatrevieux.araknemu.network.game.in.basic;

import fr.quatrevieux.araknemu.core.network.parser.Packet;
import fr.quatrevieux.araknemu.core.network.parser.ParsePacketException;
import fr.quatrevieux.araknemu.core.network.parser.SinglePacketParser;

final public class PlayerEmote implements Packet{
    final private String emote;

    public PlayerEmote(String emote) {
        this.emote = emote;
    }

    public String emote() {
        return emote;
    }

    final static public class Parser implements SinglePacketParser<PlayerEmote> {
        @Override
        public PlayerEmote parse(String input) throws ParsePacketException {
            return new PlayerEmote(input);
        }

        @Override
        public String code() {
            return "BS";
        }
    }
}
