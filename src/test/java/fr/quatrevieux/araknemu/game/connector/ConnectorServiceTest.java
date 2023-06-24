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

package fr.quatrevieux.araknemu.game.connector;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.living.entity.account.Account;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.account.AccountService;
import fr.quatrevieux.araknemu.game.account.GameAccount;
import fr.quatrevieux.araknemu.game.account.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConnectorServiceTest extends GameBaseCase {
    private ConnectorService service;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        service = new ConnectorService(
            container.get(TokenService.class),
            container.get(AccountService.class)
        );
    }

    @Test
    void isLogged() throws ContainerException {
        assertFalse(service.isLogged(1));

        GameAccount ga = new GameAccount(
            new Account(1),
            container.get(AccountService.class),
            1
        );

        ga.attach(session);

        assertTrue(service.isLogged(1));
    }

    @Test
    void token() throws ContainerException {
        String token = service.token(1);

        assertEquals(1, container.get(TokenService.class).get(token).id());
    }
}
