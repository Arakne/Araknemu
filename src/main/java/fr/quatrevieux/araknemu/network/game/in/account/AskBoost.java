package fr.quatrevieux.araknemu.network.game.in.account;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.network.in.Packet;
import fr.quatrevieux.araknemu.network.in.ParsePacketException;
import fr.quatrevieux.araknemu.network.in.SinglePacketParser;

/**
 * Boost one characteristic
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Account.as#L111
 */
final public class AskBoost implements Packet {
    final static public class Parser implements SinglePacketParser<AskBoost> {
        @Override
        public AskBoost parse(String input) throws ParsePacketException {
            return new AskBoost(
                Characteristic.fromId(
                    Integer.parseInt(input)
                )
            );
        }

        @Override
        public String code() {
            return "AB";
        }
    }

    final private Characteristic characteristic;

    public AskBoost(Characteristic characteristic) {
        this.characteristic = characteristic;
    }

    public Characteristic characteristic() {
        return characteristic;
    }
}
