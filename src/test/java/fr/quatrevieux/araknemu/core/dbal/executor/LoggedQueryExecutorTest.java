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

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;

import java.sql.SQLException;

class LoggedQueryExecutorTest {
    @Test
    void query() throws SQLException {
        Logger logger = Mockito.mock(Logger.class);
        QueryExecutor executor = Mockito.mock(QueryExecutor.class);

        LoggedQueryExecutor loggedQueryExecutor = new LoggedQueryExecutor(executor, logger);

        loggedQueryExecutor.query("my query");

        Mockito.verify(logger).debug("Execute query {}", "my query");
        Mockito.verify(executor).query("my query");
    }

    @Test
    void prepare() throws SQLException {
        Logger logger = Mockito.mock(Logger.class);
        QueryExecutor executor = Mockito.mock(QueryExecutor.class);

        LoggedQueryExecutor loggedQueryExecutor = new LoggedQueryExecutor(executor, logger);

        QueryExecutor.PreparedTask task = stmt -> null;

        loggedQueryExecutor.prepare("my query", task);

        Mockito.verify(logger).debug("Prepare query {}", "my query");
        Mockito.verify(executor).prepare("my query", task, false);
    }
}
