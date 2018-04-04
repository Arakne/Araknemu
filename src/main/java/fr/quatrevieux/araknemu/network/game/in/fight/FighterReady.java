package fr.quatrevieux.araknemu.network.game.in.fight;

import fr.quatrevieux.araknemu.network.in.Packet;
import fr.quatrevieux.araknemu.network.in.ParsePacketException;
import fr.quatrevieux.araknemu.network.in.SinglePacketParser;

/**
 * The fighter is ready to fight
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Game.as#L213
 */
final public class FighterReady implements Packet {
    final static public class Parser implements SinglePacketParser<FighterReady> {
        @Override
        public FighterReady parse(String input) throws ParsePacketException {
            return new FighterReady(input.equals("1"));
        }

        @Override
        public String code() {
            return "GR";
        }
    }

    private boolean flag;

    public FighterReady(boolean flag) {
        this.flag = flag;
    }

    public boolean ready() {
        return flag;
    }
}
