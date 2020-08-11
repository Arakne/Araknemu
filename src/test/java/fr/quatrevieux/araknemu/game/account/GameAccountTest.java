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

package fr.quatrevieux.araknemu.game.account;

import fr.quatrevieux.araknemu.common.account.Permission;
import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.living.entity.account.Account;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.network.out.ServerMessage;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.*;

class GameAccountTest extends GameBaseCase {
    @Test
    void isLogged() throws ContainerException {
        GameAccount account = new GameAccount(
            new Account(-1),
            container.get(AccountService.class),
            1
        );

        assertFalse(account.isLogged());
        account.attach(session);
        assertTrue(account.isLogged());
    }

    @Test
    void attachDetach() throws ContainerException {
        GameAccount account = new GameAccount(
            new Account(-1),
            container.get(AccountService.class),
            1
        );

        assertFalse(account.session().isPresent());
        account.attach(session);
        assertSame(session, account.session().get());
        account.detach();
        assertFalse(account.isLogged());
        assertFalse(account.session().isPresent());
    }

    @Test
    void getters() throws ContainerException {
        GameAccount account = new GameAccount(
            new Account(1),
            container.get(AccountService.class),
            1
        );

        assertEquals(1, account.id());
        assertEquals(0, account.community());
    }

    @Test
    void isGranted() throws ContainerException {
        GameAccount account = new GameAccount(
            new Account(1, "name", "password", "pseudo", EnumSet.of(Permission.ACCESS), "", ""),
            container.get(AccountService.class),
            1
        );

        assertTrue(account.isGranted(Permission.ACCESS));
        assertFalse(account.isGranted(Permission.SUPER_ADMIN));
    }

    @Test
    void kick() {
        GameAccount account = new GameAccount(
            new Account(1, "name", "password", "pseudo", EnumSet.of(Permission.ACCESS), "", ""),
            container.get(AccountService.class),
            1
        );

        account.kick(ServerMessage.inactivity());
        requestStack.assertEmpty();

        account.attach(session);

        account.kick(ServerMessage.inactivity());
        requestStack.assertLast(ServerMessage.inactivity());
        assertFalse(session.isAlive());
    }
}
