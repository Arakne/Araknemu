package fr.quatrevieux.araknemu.network.game.in.account;

import fr.quatrevieux.araknemu.network.in.Packet;
import fr.quatrevieux.araknemu.network.in.ParsePacketException;
import fr.quatrevieux.araknemu.network.in.SinglePacketParser;

/**
 * Select the character for playing
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Account.as#L84
 */
final public class ChoosePlayingCharacter implements Packet {
    final static public class Parser implements SinglePacketParser<ChoosePlayingCharacter> {
        @Override
        public ChoosePlayingCharacter parse(String input) throws ParsePacketException {
            return new ChoosePlayingCharacter(
                Integer.parseInt(input)
            );
        }

        @Override
        public String code() {
            return "AS";
        }
    }

    final private int id;

    public ChoosePlayingCharacter(int id) {
        this.id = id;
    }

    /**
     * Get the character ID
     */
    public int id() {
        return id;
    }
}
