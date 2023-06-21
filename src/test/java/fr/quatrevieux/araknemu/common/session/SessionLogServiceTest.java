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
 * Copyright (c) 2017-2020 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.common.session;

import fr.quatrevieux.araknemu.common.account.Permission;
import fr.quatrevieux.araknemu.data.living.entity.account.Account;
import fr.quatrevieux.araknemu.data.living.entity.account.ConnectionLog;
import fr.quatrevieux.araknemu.data.living.repository.account.ConnectionLogRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.account.AccountService;
import fr.quatrevieux.araknemu.game.account.GameAccount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SessionLogServiceTest extends GameBaseCase {
    private SessionLogService service;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        service = new SessionLogService(container.get(ConnectionLogRepository.class));
        dataSet.use(ConnectionLog.class);
    }

    @Test
    void create() {
        GameAccount account = new GameAccount(
            new Account(1, "toto", "", "bob", EnumSet.noneOf(Permission.class), "my question", "my response"),
            container.get(AccountService.class),
            configuration.id()
        );

        account.attach(session);

        assertNotNull(service.create(session));
        ConnectionLog log = container.get(ConnectionLogRepository.class).currentSession(session.account().id());

        assertBetween(0, 1, Instant.now().getEpochSecond() - log.startDate().getEpochSecond());
        assertEquals("127.0.0.1", log.ipAddress());
        assertEquals(session.account().id(), log.accountId());
    }

    @Test
    void loadWithoutLogShouldCreateNewOne() {
        GameAccount account = new GameAccount(
            new Account(1, "toto", "", "bob", EnumSet.noneOf(Permission.class), "my question", "my response"),
            container.get(AccountService.class),
            configuration.id()
        );

        account.attach(session);

        assertNotNull(service.load(session));
        ConnectionLog log = container.get(ConnectionLogRepository.class).currentSession(session.account().id());

        assertBetween(0, 1, Instant.now().getEpochSecond() - log.startDate().getEpochSecond());
        assertEquals("127.0.0.1", log.ipAddress());
        assertEquals(session.account().id(), log.accountId());
    }

    @Test
    void loadSuccess() {
        GameAccount account = new GameAccount(
            new Account(1, "toto", "", "bob", EnumSet.noneOf(Permission.class), "my question", "my response"),
            container.get(AccountService.class),
            configuration.id()
        );

        account.attach(session);

        ConnectionLog log = dataSet.push(new ConnectionLog(account.id(), Instant.parse("2020-06-12T12:30:00.00Z"), "12.36.54.98"));
        SessionLog sessionLog = service.load(session);

        sessionLog.setPlayerId(5);
        assertEquals(5, dataSet.refresh(log).playerId());
    }
}
