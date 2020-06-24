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

import fr.quatrevieux.araknemu.data.living.entity.account.ConnectionLog;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.data.living.repository.account.ConnectionLogRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class SessionLogTest extends GameBaseCase {
    private SessionLogService service;
    private SessionLog sessionLog;
    private ConnectionLog entity;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        service = new SessionLogService(container.get(ConnectionLogRepository.class));
        dataSet.use(ConnectionLog.class);

        sessionLog = new SessionLog(
            entity = dataSet.push(new ConnectionLog(1, Instant.parse("2020-06-12T12:30:00.00Z"), "12.36.54.98")),
            service
        );
    }

    @Test
    void setServerId() {
        sessionLog.setServerId(4);
        assertEquals(4, dataSet.refresh(entity).serverId());
    }

    @Test
    void setPlayerId() {
        sessionLog.setPlayerId(9);
        assertEquals(9, dataSet.refresh(entity).playerId());
    }

    @Test
    void stop() {
        sessionLog.stop();
        assertBetween(0, 1, Instant.now().getEpochSecond() - dataSet.refresh(entity).endDate().getEpochSecond());
    }

    @Test
    void last() {
        assertFalse(sessionLog.last().isPresent());

        ConnectionLog log = dataSet.push(new ConnectionLog(1, Instant.parse("2020-06-02T12:30:00.00Z"), "12.36.54.98"));
        log.setEndDate(Instant.parse("2020-06-02T15:30:00.00Z"));
        service.save(log);

        assertTrue(sessionLog.last().isPresent());
        assertEquals(log.startDate(), sessionLog.last().get().startDate());
        assertEquals(log.endDate(), sessionLog.last().get().endDate());
        assertEquals(log.accountId(), sessionLog.last().get().accountId());
    }

    @Test
    void setClientUid() {
        sessionLog.setClientUid("my_uid");
        assertEquals("my_uid", dataSet.refresh(entity).clientUid());
    }

    @Test
    void hasAlreadyPlayed() {
        Player player = new Player(5);

        assertFalse(sessionLog.hasAlreadyPlayed(player));
        sessionLog.setPlayerId(5);
        sessionLog.stop();

        assertTrue(sessionLog.hasAlreadyPlayed(player));
    }
}
