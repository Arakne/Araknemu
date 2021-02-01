/*
 * This file is part of Araknemu.
 *
 * Araknemu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Araknemu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Araknemu.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Copyright (c) 2017-2019 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.network.game.out.info;

import org.apache.commons.lang3.StringUtils;

/**
 * Information message base packet
 * MUST not be used directly
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Infos.as#L96
 */
public abstract class AbstractInformationMessage {
    public enum Type {
        INFO,
        ERROR,
        PVP
    }

    static final class Entry {
        private static final Object[] EMPTY = new Object[0];

        private final int id;
        private final Object[] arguments;

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

    private final Type type;
    private final Entry[] entries;

    AbstractInformationMessage(Type type, Entry... entries) {
        this.type = type;
        this.entries = entries;
    }

    @Override
    public String toString() {
        return "Im" + type.ordinal() + StringUtils.join(entries, "|");
    }
}
