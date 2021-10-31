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

package fr.quatrevieux.araknemu.realm.listener;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.network.realm.out.HostList;
import fr.quatrevieux.araknemu.realm.authentication.AuthenticationService;
import fr.quatrevieux.araknemu.realm.host.event.HostsUpdated;

/**
 * Send the new hosts list to all connected accounts
 */
public final class SendUpdatedHostList implements Listener<HostsUpdated> {
    private final AuthenticationService service;

    public SendUpdatedHostList(AuthenticationService service) {
        this.service = service;
    }

    @Override
    public void on(HostsUpdated event) {
        final HostList packet = new HostList(event.hosts());

        service.authenticatedAccounts().forEach(account -> account.send(packet));
    }

    @Override
    public Class<HostsUpdated> event() {
        return HostsUpdated.class;
    }
}
