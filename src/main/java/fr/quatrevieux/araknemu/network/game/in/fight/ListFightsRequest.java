package fr.quatrevieux.araknemu.network.game.in.fight;

import fr.quatrevieux.araknemu.network.in.Packet;
import fr.quatrevieux.araknemu.network.in.ParsePacketException;
import fr.quatrevieux.araknemu.network.in.SinglePacketParser;

/**
 * Request for list fights on map
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Fights.as#L19
 */
final public class ListFightsRequest implements Packet {
    final static public class Parser implements SinglePacketParser<ListFightsRequest> {
        @Override
        public ListFightsRequest parse(String input) throws ParsePacketException {
            return new ListFightsRequest();
        }

        @Override
        public String code() {
            return "fL";
        }
    }
}
