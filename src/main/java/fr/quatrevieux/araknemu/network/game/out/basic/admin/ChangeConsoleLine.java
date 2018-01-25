package fr.quatrevieux.araknemu.network.game.out.basic.admin;

import fr.quatrevieux.araknemu.game.admin.LogType;

/**
 * Change an admin log line
 * The changed line is relative to the log end (i.e. line 0 = last line)
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Basics.as#L125
 */
final public class ChangeConsoleLine {
    final private int line;
    final private LogType type;
    final private String message;

    public ChangeConsoleLine(int line, LogType type, String message) {
        this.line = line;
        this.type = type;
        this.message = message;
    }

    @Override
    public String toString() {
        return "BAL" + line + "|" + type.ordinal() + "|" + message;
    }
}
