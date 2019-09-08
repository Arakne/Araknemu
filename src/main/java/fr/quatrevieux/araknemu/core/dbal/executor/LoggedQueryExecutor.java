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

package fr.quatrevieux.araknemu.core.dbal.executor;

import org.slf4j.Logger;

import java.sql.SQLException;

/**
 * Logging executed queries
 */
final public class LoggedQueryExecutor implements QueryExecutor {
    final private QueryExecutor executor;
    final private Logger logger;

    public LoggedQueryExecutor(QueryExecutor executor, Logger logger) {
        this.executor = executor;
        this.logger = logger;
    }

    @Override
    public <T> T prepare(String sql, PreparedTask<T> task, boolean returnGeneratedKeys) throws SQLException {
        logger.debug("Prepare query {}", sql);

        return executor.prepare(sql, task, returnGeneratedKeys);
    }

    @Override
    public void query(String sql) throws SQLException {
        logger.debug("Execute query {}", sql);

        executor.query(sql);
    }
}
