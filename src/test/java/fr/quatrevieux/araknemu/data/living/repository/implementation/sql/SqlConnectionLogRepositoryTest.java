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

package fr.quatrevieux.araknemu.data.living.repository.implementation.sql;

import fr.quatrevieux.araknemu.core.dbal.executor.ConnectionPoolExecutor;
import fr.quatrevieux.araknemu.core.dbal.repository.EntityNotFoundException;
import fr.quatrevieux.araknemu.data.living.entity.account.Account;
import fr.quatrevieux.araknemu.data.living.entity.account.ConnectionLog;
import fr.quatrevieux.araknemu.data.living.transformer.InstantTransformer;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class SqlConnectionLogRepositoryTest extends GameBaseCase {
    private SqlConnectionLogRepository repository;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.use(ConnectionLog.class);

        repository = new SqlConnectionLogRepository(
            new ConnectionPoolExecutor(app.database().get("game")),
            new InstantTransformer()
        );
    }

    @Test
    void getNotFound() {
        assertThrows(EntityNotFoundException.class, () -> repository.get(new ConnectionLog(1, Instant.now(), "")));
    }

    @Test
    void has() {
        ConnectionLog log = new ConnectionLog(1, Instant.parse("2020-06-05T14:25:31.00Z"), "145.25.211.5");

        assertFalse(repository.has(log));

        repository.add(log);
        assertTrue(repository.has(log));
    }

    @Test
    void addAndGet() {
        ConnectionLog log = new ConnectionLog(1, Instant.parse("2020-06-05T14:25:31.00Z"), "145.25.211.5");
        assertSame(log, repository.add(log));

        ConnectionLog savedLog = repository.get(log);

        assertEquals(log.accountId(), savedLog.accountId());
        assertEquals(log.startDate(), savedLog.startDate());
        assertEquals(log.ipAddress(), savedLog.ipAddress());
        assertNull(savedLog.playerId());
        assertNull(savedLog.serverId());
        assertNull(savedLog.endDate());
    }

    @Test
    void save() {
        ConnectionLog log = new ConnectionLog(1, Instant.parse("2020-06-05T14:25:31.00Z"), "145.25.211.5");
        assertSame(log, repository.add(log));

        repository.save(log);
        log = repository.get(log);

        assertNull(log.playerId());
        assertNull(log.serverId());
        assertNull(log.endDate());

        log.setServerId(5);
        repository.save(log);
        log = repository.get(log);

        assertNull(log.playerId());
        assertEquals(5, log.serverId());
        assertNull(log.endDate());

        log.setPlayerId(12);
        repository.save(log);
        log = repository.get(log);

        assertEquals(12, log.playerId());
        assertEquals(5, log.serverId());
        assertNull(log.endDate());

        log.setEndDate(Instant.parse("2020-06-05T16:10:21.00Z"));
        repository.save(log);
        log = repository.get(log);

        assertEquals(12, log.playerId());
        assertEquals(5, log.serverId());
        assertEquals(Instant.parse("2020-06-05T16:10:21.00Z"), log.endDate());
    }

    @Test
    void clientUid() {
        ConnectionLog log = new ConnectionLog(1, Instant.parse("2020-06-05T14:25:31.00Z"), "145.25.211.5");
        repository.add(log);

        assertNull(repository.get(log).clientUid());

        log.setClientUid("my_uid");
        repository.save(log);
        assertEquals("my_uid", repository.get(log).clientUid());
    }

    @Test
    void lastSession() {
        Account account = new Account(5);

        assertFalse(repository.lastSession(account.id()).isPresent());

        ConnectionLog log = repository.add(new ConnectionLog(5, Instant.parse("2020-06-05T16:10:21.00Z"), ""));
        assertFalse(repository.lastSession(account.id()).isPresent());

        log.setEndDate(Instant.parse("2020-06-05T16:15:21.00Z"));
        repository.save(log);

        ConnectionLog other = repository.add(new ConnectionLog(5, Instant.parse("2020-06-05T17:00:21.00Z"), ""));
        assertEquals(log.startDate(), repository.lastSession(account.id()).get().startDate());

        other.setEndDate(Instant.parse("2020-06-05T18:15:21.00Z"));
        repository.save(other);

        assertEquals(other.startDate(), repository.lastSession(account.id()).get().startDate());
    }

    @Test
    void currentSession() {
        assertThrows(EntityNotFoundException.class, () -> repository.currentSession(5));

        ConnectionLog log = repository.add(new ConnectionLog(5, Instant.parse("2020-06-05T16:10:21.00Z"), ""));
        assertEquals(log.startDate(), repository.currentSession(5).startDate());

        ConnectionLog recentLog = repository.add(new ConnectionLog(5, Instant.parse("2020-06-06T16:10:21.00Z"), ""));
        assertEquals(recentLog.startDate(), repository.currentSession(5).startDate());

        log.setEndDate(Instant.now());
        recentLog.setEndDate(Instant.now());

        repository.save(log);
        repository.save(recentLog);

        assertThrows(EntityNotFoundException.class, () -> repository.currentSession(5));
    }

    @Test
    void delete() {
        ConnectionLog log = repository.add(new ConnectionLog(5, Instant.parse("2020-06-05T16:10:21.00Z"), ""));
        repository.delete(log);

        assertThrows(EntityNotFoundException.class, () -> repository.get(log));
    }
}
