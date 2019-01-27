package fr.quatrevieux.araknemu.network.game.in.dialog;

import fr.quatrevieux.araknemu.network.in.Packet;
import fr.quatrevieux.araknemu.network.in.ParsePacketException;
import fr.quatrevieux.araknemu.network.in.SinglePacketParser;

/**
 * Stops a dialog with a NPC
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Dialog.as#L27
 */
final public class LeaveDialogRequest implements Packet {
    final static public class Parser implements SinglePacketParser<LeaveDialogRequest> {
        @Override
        public LeaveDialogRequest parse(String input) throws ParsePacketException {
            return new LeaveDialogRequest();
        }

        @Override
        public String code() {
            return "DV";
        }
    }
}
