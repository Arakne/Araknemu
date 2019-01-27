package fr.quatrevieux.araknemu.network.game.in.dialog;

import fr.quatrevieux.araknemu.network.in.Packet;
import fr.quatrevieux.araknemu.network.in.ParsePacketException;
import fr.quatrevieux.araknemu.network.in.SinglePacketParser;
import org.apache.commons.lang3.StringUtils;

/**
 * A dialog response is chosen by the player
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Dialog.as#L31
 */
final public class ChosenResponse implements Packet {
    final static public class Parser implements SinglePacketParser<ChosenResponse> {
        @Override
        public ChosenResponse parse(String input) throws ParsePacketException {
            final String[] parts = StringUtils.split(input, "|", 2);

            if (parts.length != 2) {
                throw new ParsePacketException(code() + input, "Expects two parts");
            }

            return new ChosenResponse(
                Integer.parseInt(parts[0]),
                Integer.parseInt(parts[1])
            );
        }

        @Override
        public String code() {
            return "DR";
        }
    }

    final private int question;
    final private int response;

    public ChosenResponse(int question, int response) {
        this.question = question;
        this.response = response;
    }

    public int question() {
        return question;
    }

    public int response() {
        return response;
    }
}
