package fr.quatrevieux.araknemu.network.game.out.basic.admin;

import fr.quatrevieux.araknemu.game.admin.LogType;

/**
 * Send the result of the admin command
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Basics.as#L78
 */
final public class CommandResult {
    final private LogType type;
    final private String message;

    public CommandResult(LogType type, String message) {
        this.type = type;
        this.message = message;
    }

    @Override
    public String toString() {
        return "BAT" + type.ordinal() + message;
    }
}
