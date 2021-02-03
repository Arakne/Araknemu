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

package fr.quatrevieux.araknemu.data.value;

/**
 * Value object for get characters count per server
 */
public final class ServerCharacters {
    private final int serverId;
    private final int charactersCount;

    public ServerCharacters(int serverId, int charactersCount) {
        this.serverId = serverId;
        this.charactersCount = charactersCount;
    }

    public int serverId() {
        return serverId;
    }

    public int charactersCount() {
        return charactersCount;
    }
}
