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

package fr.quatrevieux.araknemu.data.living.repository.account;

import fr.quatrevieux.araknemu.core.dbal.repository.MutableRepository;
import fr.quatrevieux.araknemu.data.living.entity.account.ConnectionLog;

import java.util.Optional;

/**
 * Repository for {@link ConnectionLog}
 */
public interface ConnectionLogRepository extends MutableRepository<ConnectionLog> {
    /**
     * Get the last terminated session for the given account
     * Note: do not return the current session
     *
     * @param accountId Tha account to search on
     *
     * @return The last session, if fount
     */
    public Optional<ConnectionLog> lastSession(int accountId);

    /**
     * Get the current session log for the given account
     * The current session is the last session without end date
     *
     * @param accountId The account to search on
     *
     * @return The connection log
     * @throws fr.quatrevieux.araknemu.core.dbal.repository.EntityNotFoundException When the log is not found
     */
    public ConnectionLog currentSession(int accountId);

    /**
     * Save mutable fields of the log
     */
    public void save(ConnectionLog log);
}
