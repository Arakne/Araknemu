package fr.quatrevieux.araknemu.network.game.in.basic;

import fr.quatrevieux.araknemu.core.network.parser.Packet;
import fr.quatrevieux.araknemu.core.network.parser.ParsePacketException;
import fr.quatrevieux.araknemu.core.network.parser.SinglePacketParser;

final public class SetEmoteRequest implements Packet{
    final private String emote;

    public SetEmoteRequest(String emote) {
        this.emote = emote;
    }

    public String emote() {
        return emote;
    }

    final static public class Parser implements SinglePacketParser<SetEmoteRequest> {
        @Override
        public SetEmoteRequest parse(String input) throws ParsePacketException {
            return new SetEmoteRequest(input);
        }

        @Override
        public String code() {
            return "BS";
        }
    }
}
