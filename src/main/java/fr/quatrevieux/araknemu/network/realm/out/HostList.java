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

package fr.quatrevieux.araknemu.network.realm.out;

import fr.quatrevieux.araknemu.realm.host.GameHost;

import java.util.Collection;

/**
 * Send list of game servers
 */
public final class HostList {
    private final Collection<GameHost> list;

    public HostList(Collection<GameHost> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("AH");

        boolean first = true;

        for (GameHost host : list) {
            if (first) {
                first = false;
            } else {
                sb.append('|');
            }

            sb.append(host.id()).append(';')
                .append(host.state().ordinal()).append(';')
                .append(host.completion()).append(';')
                .append(host.canLog() ? 1 : 0)
            ;
        }

        return sb.toString();
    }
}
