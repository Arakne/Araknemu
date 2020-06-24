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

import fr.quatrevieux.araknemu.core.dbal.repository.EntityNotFoundException;
import fr.quatrevieux.araknemu.data.living.entity.account.ConnectionLog;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.data.living.repository.account.ConnectionLogRepository;
import fr.quatrevieux.araknemu.network.AccountSession;

import java.time.Instant;
import java.util.Optional;

/**
 * Handle connections logs
 */
final public class SessionLogService {
    final private ConnectionLogRepository repository;

    public SessionLogService(ConnectionLogRepository repository) {
        this.repository = repository;
    }

    /**
     * Load the current log the the given session
     *
     * @param session Session to load
     *
     * @return The log from database, or a new log is not exists
     */
    public SessionLog load(AccountSession<?> session) {
        try {
            return new SessionLog(
                repository.currentSession(session.account().id()),
                this
            );
        } catch (EntityNotFoundException e) {
            return create(session);
        }
    }

    /**
     * Create a new session log for the given session
     *
     * @param session The session
     */
    public SessionLog create(AccountSession<?> session) {
        return new SessionLog(
            repository.add(new ConnectionLog(
                session.account().id(),
                Instant.now(),
                session.channel().address().getAddress().getHostAddress()
            )),
            this
        );
    }

    /**
     * Save log to database
     */
    void save(ConnectionLog log) {
        repository.save(log);
    }

    /**
     * Get the last session
     */
    Optional<ConnectionLog> lastSession(int accountId) {
        return repository.lastSession(accountId);
    }

    /**
     * Check if a session is present for the given player
     */
    boolean hasAlreadyPlayed(Player player) {
        return repository.hasAlreadyPlayed(player);
    }
}
