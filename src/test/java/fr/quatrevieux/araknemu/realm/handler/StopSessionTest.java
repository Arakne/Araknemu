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

package fr.quatrevieux.araknemu.realm.handler;

import fr.quatrevieux.araknemu.core.network.SessionClosed;
import fr.quatrevieux.araknemu.data.living.entity.account.Account;
import fr.quatrevieux.araknemu.data.living.repository.account.AccountRepository;
import fr.quatrevieux.araknemu.realm.RealmBaseCase;
import fr.quatrevieux.araknemu.realm.authentication.AuthenticationAccount;
import fr.quatrevieux.araknemu.realm.authentication.AuthenticationService;
import fr.quatrevieux.araknemu.realm.host.HostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

class StopSessionTest extends RealmBaseCase {
    private StopSession handler;
    private AuthenticationService service;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        handler = new StopSession(
            service = new AuthenticationService(
                container.get(AccountRepository.class),
                container.get(HostService.class)
            )
        );
    }

    @Test
    void handleNotLogged() {
        handler.handle(session, new SessionClosed());
    }

    @Test
    void handleWillLogout() {
        AuthenticationAccount account = new AuthenticationAccount(
            new Account(1),
            service
        );

        account.attach(session);

        handler.handle(session, new SessionClosed());

        assertFalse(session.isLogged());
        assertFalse(service.isAuthenticated(account));
    }
}