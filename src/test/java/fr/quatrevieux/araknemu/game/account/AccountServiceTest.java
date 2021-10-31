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
import fr.quatrevieux.araknemu.common.account.banishment.BanEntry;
import fr.quatrevieux.araknemu.common.account.banishment.BanishmentService;
import fr.quatrevieux.araknemu.common.account.banishment.event.AccountBanned;
import fr.quatrevieux.araknemu.core.dbal.repository.EntityNotFoundException;
import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.core.event.DefaultListenerAggregate;
import fr.quatrevieux.araknemu.core.event.ListenerAggregate;
import fr.quatrevieux.araknemu.data.living.entity.account.Account;
import fr.quatrevieux.araknemu.data.living.entity.account.Banishment;
import fr.quatrevieux.araknemu.data.living.repository.account.AccountRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.GameConfiguration;
import fr.quatrevieux.araknemu.network.game.out.basic.admin.TemporaryRightsGranted;
import fr.quatrevieux.araknemu.network.game.out.basic.admin.TemporaryRightsRevoked;
import fr.quatrevieux.araknemu.network.out.ServerMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.Duration;
import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AccountServiceTest extends GameBaseCase {
    private AccountService service;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        service = new AccountService(
            container.get(AccountRepository.class),
            container.get(GameConfiguration.class)
        );

        dataSet.use(Account.class);
    }

    @Test
    void loadAccountNotFound() {
        assertThrows(EntityNotFoundException.class, () -> service.load(new Account(-1)));
    }

    @Test
    void loadSuccess() throws ContainerException {
        Account account = new Account(1, "name", "pass", "pseudo");
        dataSet.push(account);

        GameAccount ga = service.load(new Account(1));

        assertEquals(1, ga.id());
        assertEquals(2, ga.serverId());
    }

    @Test
    void isLogged() {
        assertFalse(service.isLogged(1));

        GameAccount account = new GameAccount(
            new Account(1),
            service,
            1
        );

        account.attach(session);
        assertTrue(service.isLogged(1));
    }

    @Test
    void getByIds() {
        Account account1 = dataSet.push(new Account(-1, "name", "pass", "pseudo", Collections.emptySet(), "", ""));
        Account account2 = dataSet.push(new Account(-1, "name2", "pass", "pseudo2", Collections.emptySet(), "", ""));

        GameAccount account = new GameAccount(
            account1,
            service,
            1
        );
        account.attach(session);

        Map<Integer, GameAccount> accounts = service.getByIds(new int[] {account1.id(), account2.id(), -1});

        assertEquals(2, accounts.size());
        assertSame(account, accounts.get(account1.id()));
        assertEquals("pseudo2", accounts.get(account2.id()).pseudo());
    }

    @Test
    void findByPseudoLoggedAccount() {
        GameAccount account = new GameAccount(
            dataSet.push(new Account(-1, "name", "pass", "pseudo", Collections.emptySet(), "", "")),
            service,
            1
        );
        account.attach(session);

        assertSame(account, service.findByPseudo("pseudo").get());
    }

    @Test
    void findByPseudoNotLoggedAccount() {
        Account account = dataSet.push(new Account(-1, "name", "pass", "pseudo", Collections.emptySet(), "", ""));

        assertEquals(account.id(), service.findByPseudo("pseudo").get().id());
    }

    @Test
    void findByPseudoNotFound() {
        assertFalse(service.findByPseudo("not_found").isPresent());
    }

    @Test
    void listenerShouldKickAccountOnBan() {
        dataSet.use(Banishment.class);
        Account account1 = dataSet.push(new Account(-1, "name", "pass", "pseudo", Collections.emptySet(), "", ""));
        GameAccount account = new GameAccount(account1, service, 1);
        account.attach(session);

        GameAccount banisher = new GameAccount(new Account(-1, "banisher", "pass", "banisher", Collections.emptySet(), "", ""), service, 1);

        BanEntry<GameAccount> entry = ((BanishmentService<GameAccount>) container.get(BanishmentService.class)).ban(account, Duration.ofHours(1), "cause", banisher);

        ListenerAggregate dispatcher = new DefaultListenerAggregate();
        dispatcher.register(service);

        dispatcher.dispatch(new AccountBanned<>(entry));

        requestStack.assertLast(ServerMessage.kick(banisher.pseudo(), "cause"));
        assertFalse(session.isAlive());
    }

    @Test
    void listenerShouldKickAccountOnBanWithoutBanisher() {
        dataSet.use(Banishment.class);
        Account account1 = dataSet.push(new Account(-1, "name", "pass", "pseudo", Collections.emptySet(), "", ""));
        GameAccount account = new GameAccount(account1, service, 1);
        account.attach(session);

        BanEntry<GameAccount> entry = ((BanishmentService<GameAccount>) container.get(BanishmentService.class)).ban(account, Duration.ofHours(1), "cause");

        ListenerAggregate dispatcher = new DefaultListenerAggregate();
        dispatcher.register(service);

        dispatcher.dispatch(new AccountBanned<>(entry));

        requestStack.assertLast(ServerMessage.kick("system", "cause"));
        assertFalse(session.isAlive());
    }

    @Test
    void listenerShouldSendAdminAccessOnPermissionChanged() throws SQLException {
        GameAccount account = gamePlayer(true).account();
        requestStack.clear();

        GameAccount admin = new GameAccount(new Account(-1, "admin", "pass", "admin", Collections.emptySet(), "", ""), service, 1);

        account.grant(new Permission[] {Permission.MANAGE_PLAYER}, admin);
        requestStack.assertLast(new TemporaryRightsGranted("admin"));

        account.revoke(admin);
        requestStack.assertLast(new TemporaryRightsRevoked("admin"));
        requestStack.clear();

        account.revoke(admin);
        requestStack.assertEmpty();
    }
}
