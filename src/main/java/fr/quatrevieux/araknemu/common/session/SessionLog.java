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

import java.time.Instant;
import java.util.Optional;

/**
 * Handle log for the current session
 */
final public class SessionLog {
    final private ConnectionLog log;
    final private SessionLogService service;

    SessionLog(ConnectionLog log, SessionLogService service) {
        this.log = log;
        this.service = service;
    }

    /**
     * Define the current server id
     */
    public void setServerId(int serverId) {
        log.setServerId(serverId);
        service.save(log);
    }

    /**
     * Define the current player id
     */
    public void setPlayerId(int playerId) {
        log.setPlayerId(playerId);
        service.save(log);
    }

    /**
     * Define the current client uid
     */
    public void setClientUid(String uid) {
        log.setClientUid(uid);
        service.save(log);
    }

    /**
     * Mark the session as terminated
     */
    public void stop() {
        log.setEndDate(Instant.now());
        service.save(log);
    }

    /**
     * Get the last session, if present
     */
    public Optional<ConnectionLog> last() {
        return service.lastSession(log.accountId());
    }
}
