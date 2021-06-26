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
 * Copyright (c) 2017-2021 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.realm.host.event;

import fr.quatrevieux.araknemu.realm.host.GameHost;

import java.util.Collection;

/**
 * Trigger when the hosts list is updated :
 * - A new game host is declared
 * - An host change its state
 */
public final class HostsUpdated {
    private final Collection<GameHost> hosts;

    public HostsUpdated(Collection<GameHost> hosts) {
        this.hosts = hosts;
    }

    /**
     * Get the updated hosts list
     */
    public Collection<GameHost> hosts() {
        return hosts;
    }
}
