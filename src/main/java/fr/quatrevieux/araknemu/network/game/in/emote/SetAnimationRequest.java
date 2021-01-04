package fr.quatrevieux.araknemu.network.game.in.emote;

import java.util.jar.Pack200.Packer;

import fr.quatrevieux.araknemu.core.network.parser.Packet;
import fr.quatrevieux.araknemu.core.network.parser.ParsePacketException;
import fr.quatrevieux.araknemu.core.network.parser.SinglePacketParser;

final public class SetAnimationRequest implements Packet{
    final static public class Parser implements SinglePacketParser<SetAnimationRequest> {
        @Override
        public SetAnimationRequest parse(String input) throws ParsePacketException {
            return new SetAnimationRequest(input);
        }

        @Override
        public String code() {
            return "eU";
        }
    }

    final private String animation;
    public SetAnimationRequest(String animation) {
        this.animation = animation;
    }

    public String animation() {
        return animation;
    }
}
