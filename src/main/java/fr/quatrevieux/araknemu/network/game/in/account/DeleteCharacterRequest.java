package fr.quatrevieux.araknemu.network.game.in.account;

import fr.quatrevieux.araknemu.network.in.Packet;
import fr.quatrevieux.araknemu.network.in.ParsePacketException;
import fr.quatrevieux.araknemu.network.in.SinglePacketParser;
import org.apache.commons.lang3.StringUtils;

/**
 * Ask for character deletion
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Account.as#L94
 */
final public class DeleteCharacterRequest implements Packet {
    final static public class Parser implements SinglePacketParser<DeleteCharacterRequest> {
        @Override
        public DeleteCharacterRequest parse(String input) throws ParsePacketException {
            String[] parts = StringUtils.split(input, "|", 2);

            return new DeleteCharacterRequest(
                Integer.parseInt(parts[0]),
                parts.length == 2 ? parts[1] : ""
            );
        }

        @Override
        public String code() {
            return "AD";
        }
    }

    final private int id;
    final private String answer;

    public DeleteCharacterRequest(int id, String answer) {
        this.id = id;
        this.answer = answer;
    }

    public int id() {
        return id;
    }

    public String answer() {
        return answer;
    }
}
