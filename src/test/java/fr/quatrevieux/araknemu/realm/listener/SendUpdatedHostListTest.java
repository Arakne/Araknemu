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

import fr.quatrevieux.araknemu.data.living.entity.account.Account;
import fr.quatrevieux.araknemu.network.realm.out.HostList;
import fr.quatrevieux.araknemu.realm.RealmBaseCase;
import fr.quatrevieux.araknemu.realm.authentication.AuthenticationAccount;
import fr.quatrevieux.araknemu.realm.authentication.AuthenticationService;
import fr.quatrevieux.araknemu.realm.authentication.password.PlainTextHash;
import fr.quatrevieux.araknemu.realm.host.HostService;
import fr.quatrevieux.araknemu.realm.host.event.HostsUpdated;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SendUpdatedHostListTest extends RealmBaseCase {
    private SendUpdatedHostList listener;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        listener = new SendUpdatedHostList(container.get(AuthenticationService.class));
    }

    @Test
    void onHostsUpdated() {
        AuthenticationAccount account = new AuthenticationAccount(
            new Account(1),
            new PlainTextHash().parse("password"),
            container.get(AuthenticationService.class)
        );

        account.attach(session);

        listener.on(new HostsUpdated(container.get(HostService.class).all()));

        requestStack.assertLast(new HostList(container.get(HostService.class).all()));
    }
}
