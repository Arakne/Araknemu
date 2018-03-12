package fr.quatrevieux.araknemu.network.game.in.spell;

import fr.quatrevieux.araknemu.network.in.Packet;
import fr.quatrevieux.araknemu.network.in.ParsePacketException;
import fr.quatrevieux.araknemu.network.in.SinglePacketParser;
import org.apache.commons.lang3.StringUtils;

/**
 * Spell move to spell bar
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Spells.as#L19
 */
final public class SpellMove implements Packet {
    final static public class Parser implements SinglePacketParser<SpellMove> {
        @Override
        public SpellMove parse(String input) throws ParsePacketException {
            String[] parts = StringUtils.split(input, "|", 2);

            return new SpellMove(
                Integer.parseUnsignedInt(parts[0]),
                Integer.parseUnsignedInt(parts[1])
            );
        }

        @Override
        public String code() {
            return "SM";
        }
    }

    final private int spellId;
    final private int position;

    public SpellMove(int spellId, int position) {
        this.spellId = spellId;
        this.position = position;
    }

    public int spellId() {
        return spellId;
    }

    public int position() {
        return position;
    }
}
