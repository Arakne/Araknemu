package fr.quatrevieux.araknemu.network.game.in.basic.admin;

import fr.quatrevieux.araknemu.network.in.Packet;
import fr.quatrevieux.araknemu.network.in.ParsePacketException;
import fr.quatrevieux.araknemu.network.in.SinglePacketParser;

/**
 * Admin console command
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Basics.as#L21
 */
final public class AdminCommand implements Packet {
    final static public class Parser implements SinglePacketParser<AdminCommand> {
        @Override
        public AdminCommand parse(String input) throws ParsePacketException {
            return new AdminCommand(input);
        }

        @Override
        public String code() {
            return "BA";
        }
    }

    final private String command;

    public AdminCommand(String command) {
        this.command = command;
    }

    public String command() {
        return command;
    }
}
