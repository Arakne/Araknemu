package fr.quatrevieux.araknemu.network.game.in.spell;

import fr.quatrevieux.araknemu.network.in.Packet;
import fr.quatrevieux.araknemu.network.in.ParsePacketException;
import fr.quatrevieux.araknemu.network.in.SinglePacketParser;

/**
 * Ask for upgrade a spell
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Spells.as#L23
 */
final public class SpellUpgrade implements Packet {
    final static public class Parser implements SinglePacketParser<SpellUpgrade> {
        @Override
        public SpellUpgrade parse(String input) throws ParsePacketException {
            return new SpellUpgrade(Integer.parseUnsignedInt(input));
        }

        @Override
        public String code() {
            return "SB";
        }
    }

    final private int spellId;

    public SpellUpgrade(int spellId) {
        this.spellId = spellId;
    }

    public int spellId() {
        return spellId;
    }
}
