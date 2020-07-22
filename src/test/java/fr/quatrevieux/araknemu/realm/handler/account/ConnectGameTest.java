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

package fr.quatrevieux.araknemu.realm.handler.account;

import fr.quatrevieux.araknemu.data.living.entity.account.Account;
import fr.quatrevieux.araknemu.network.realm.in.ChooseServer;
import fr.quatrevieux.araknemu.network.realm.out.SelectServerError;
import fr.quatrevieux.araknemu.network.realm.out.SelectServerPlain;
import fr.quatrevieux.araknemu.realm.RealmBaseCase;
import fr.quatrevieux.araknemu.realm.authentication.AuthenticationAccount;
import fr.quatrevieux.araknemu.realm.authentication.AuthenticationService;
import fr.quatrevieux.araknemu.realm.authentication.password.PlainTextHash;
import fr.quatrevieux.araknemu.realm.host.HostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConnectGameTest extends RealmBaseCase {
    private ConnectGame handler;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        handler = new ConnectGame(
            container.get(HostService.class)
        );

        session.attach(
            new AuthenticationAccount(
                new Account(1),
                new PlainTextHash().parse("password"),
                container.get(AuthenticationService.class)
            )
        );
    }

    @Test
    void handleInvalidServer() {
        handler.handle(session, new ChooseServer(10));
        requestStack.assertLast(new SelectServerError(SelectServerError.Error.CANT_SELECT));
    }

    @Test
    void handleSuccess() {
        gameHost.setCanLog(true);
        connector.token = "my_token";

        handler.handle(session, new ChooseServer(1));

        requestStack.assertLast(new SelectServerPlain("127.0.0.1", 1234, "my_token"));
        assertEquals(session.account(), connector.account);
        assertFalse(session.isAlive());
    }
}
