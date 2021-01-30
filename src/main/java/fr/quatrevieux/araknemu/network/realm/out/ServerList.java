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

import fr.quatrevieux.araknemu.data.value.ServerCharacters;

import java.util.Collection;

/**
 * Send list of used server (with characters count)
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Account.as#L372
 */
final public class ServerList {
    final static public long ONE_YEAR = 31536000000L;
    
    final private long aboTime;
    final private Collection<ServerCharacters> servers;

    public ServerList(long aboTime, Collection<ServerCharacters> servers) {
        this.aboTime = aboTime;
        this.servers = servers;
    }

    @Override
    public String toString() {
        final StringBuilder ret = new StringBuilder(64);
        
        ret.append("AxK").append(aboTime);
        
        for (ServerCharacters server : servers) {
            ret.append('|').append(server.serverId()).append(',').append(server.charactersCount());
        }
        
        return ret.toString();
    }
}
