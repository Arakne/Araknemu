package fr.quatrevieux.araknemu.network.game.in.fight;

import fr.quatrevieux.araknemu.network.in.Packet;
import fr.quatrevieux.araknemu.network.in.ParsePacketException;
import fr.quatrevieux.araknemu.network.in.SinglePacketParser;

/**
 * Change fight start place
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Game.as#L31
 */
final public class FighterChangePlace implements Packet {
    final static public class Parser implements SinglePacketParser<FighterChangePlace> {
        @Override
        public FighterChangePlace parse(String input) throws ParsePacketException {
            return new FighterChangePlace(Integer.parseUnsignedInt(input));
        }

        @Override
        public String code() {
            return "Gp";
        }
    }

    final private int cellId;

    public FighterChangePlace(int cellId) {
        this.cellId = cellId;
    }

    public int cellId() {
        return cellId;
    }
}
