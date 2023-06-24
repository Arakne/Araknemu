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
import fr.quatrevieux.araknemu.game.account.event.AccountPermissionsUpdated;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.out.ServerMessage;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.EnumSet;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    @Test
    void grantAndRevokeWithoutSession() throws ContainerException {
        GameAccount account = new GameAccount(
            new Account(1, "name", "password", "pseudo", EnumSet.noneOf(Permission.class), "", ""),
            container.get(AccountService.class),
            1
        );

        assertFalse(account.isGranted(Permission.ACCESS));
        assertFalse(account.isMaster());

        account.grant(Permission.ACCESS);

        assertTrue(account.isGranted(Permission.ACCESS));
        assertTrue(account.isMaster());

        account.revoke(null);

        assertFalse(account.isGranted(Permission.ACCESS));
        assertFalse(account.isMaster());
    }

    @Test
    void grantAndRevokeWithSessionShouldTriggerAccountPermissionsUpdated() throws ContainerException, SQLException {
        GameSession session = server.createSession();
        GamePlayer player = makeSimpleGamePlayer(10, session, true);
        GameAccount account = player.account();

        AtomicReference<AccountPermissionsUpdated> ref = new AtomicReference<>();
        player.dispatcher().add(AccountPermissionsUpdated.class, ref::set);

        account.grant(Permission.ACCESS);
        assertSame(account, ref.get().account());
        assertFalse(ref.get().performer().isPresent());
        assertTrue(ref.get().authorized());

        account.revoke(null);
        assertSame(account, ref.get().account());
        assertFalse(ref.get().performer().isPresent());
        assertFalse(ref.get().authorized());
    }

    @Test
    void grantAndRevokeWithSessionShouldTriggerAccountPermissionsUpdatedWithPerformer() throws ContainerException, SQLException {
        GameSession session = server.createSession();
        GamePlayer player = makeSimpleGamePlayer(10, session, true);
        GameAccount account = player.account();

        AtomicReference<AccountPermissionsUpdated> ref = new AtomicReference<>();
        player.dispatcher().add(AccountPermissionsUpdated.class, ref::set);

        account.grant(new Permission[] {Permission.ACCESS}, gamePlayer().account());
        assertSame(account, ref.get().account());
        assertSame(gamePlayer().account(), ref.get().performer().get());
        assertTrue(ref.get().authorized());

        account.revoke(gamePlayer().account());
        assertSame(account, ref.get().account());
        assertSame(gamePlayer().account(), ref.get().performer().get());
        assertFalse(ref.get().authorized());
    }

    @Test
    void revokeWithoutTemporaryPermissionsShouldNotDispatchEvent() throws SQLException {
        GameSession session = server.createSession();
        GamePlayer player = makeSimpleGamePlayer(10, session, true);
        GameAccount account = player.account();

        AtomicReference<AccountPermissionsUpdated> ref = new AtomicReference<>();
        player.dispatcher().add(AccountPermissionsUpdated.class, ref::set);

        account.revoke(gamePlayer().account());
        assertNull(ref.get());
    }
}
