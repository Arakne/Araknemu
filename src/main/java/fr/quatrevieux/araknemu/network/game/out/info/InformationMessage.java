package fr.quatrevieux.araknemu.network.game.out.info;

import org.apache.commons.lang3.StringUtils;

/**
 * Information message base packet
 * MUST not be used directly
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Infos.as#L96
 */
abstract public class InformationMessage {
    public enum Type {
        INFO,
        ERROR,
        PVP
    }

    final static class Entry {
        final static private Object[] EMPTY = new Object[0];

        final private int id;
        final private Object[] arguments;

        Entry(int id, Object... arguments) {
            this.id = id;
            this.arguments = arguments;
        }

        Entry(int id) {
            this(id, EMPTY);
        }

        @Override
        public String toString() {
            return id + ";" + StringUtils.join(arguments, "~");
        }
    }

    final private Type type;
    final private Entry[] entries;

    InformationMessage(Type type, Entry... entries) {
        this.type = type;
        this.entries = entries;
    }

    @Override
    public String toString() {
        return "Im" + type.ordinal() + StringUtils.join(entries, "|");
    }
}
