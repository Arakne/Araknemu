package fr.quatrevieux.araknemu.network.game.in.dialog;

import fr.quatrevieux.araknemu.network.in.Packet;
import fr.quatrevieux.araknemu.network.in.ParsePacketException;
import fr.quatrevieux.araknemu.network.in.SinglePacketParser;

/**
 * Starts a dialog with a NPC
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Dialog.as#L23
 */
final public class CreateDialogRequest implements Packet {
    final static public class Parser implements SinglePacketParser<CreateDialogRequest> {
        @Override
        public CreateDialogRequest parse(String input) throws ParsePacketException {
            return new CreateDialogRequest(Integer.parseInt(input));
        }

        @Override
        public String code() {
            return "DC";
        }
    }

    final private int npcId;

    public CreateDialogRequest(int npcId) {
        this.npcId = npcId;
    }

    public int npcId() {
        return npcId;
    }
}
