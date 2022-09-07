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

package fr.quatrevieux.araknemu.core.dbal;

import fr.quatrevieux.araknemu.core.config.DefaultConfiguration;
import fr.quatrevieux.araknemu.core.config.IniDriver;
import org.ini4j.Ini;
import org.junit.jupiter.api.Test;
import org.sqlite.SQLiteConnection;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

class SQLiteDriverTest {
    @Test
    void newConnectionInMemory() throws SQLException, IOException {
        SQLiteDriver driver = new SQLiteDriver(
            new DefaultConfiguration(new IniDriver(new Ini(new File("src/test/test_config.ini"))))
                .module(DatabaseConfiguration.MODULE)
                .connection("realm")
        );

        try (Connection connection = driver.newConnection()) {
            assertTrue(connection instanceof SQLiteConnection);

            try (Statement stmt = connection.createStatement()) {
                assertEquals(0, stmt.executeUpdate("create table test_table (`value` text)"));
            }

            try (Statement stmt = connection.createStatement()) {
                assertEquals(1, stmt.executeUpdate("insert into test_table values ('FOO')"));
            }

            try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery("select * from test_table")){
                assertTrue(rs.next());
                assertEquals("FOO", rs.getString("value"));
            }

            try (Statement stmt = connection.createStatement()) {
                stmt.execute("drop table test_table");
            }
        }
    }

    @Test
    void sharedConnection() throws SQLException, IOException {
        SQLiteDriver driver = new SQLiteDriver(
            new DefaultConfiguration(new IniDriver(new Ini(new File("src/test/test_config.ini"))))
                .module(DatabaseConfiguration.MODULE)
                .connection("realm")
        );

        try (
            Connection c1 = driver.newConnection();
            Connection c2 = driver.newConnection()
        ) {
            try (Statement stmt = c1.createStatement()) {
                stmt.execute("create table test_table (`value` text)");
                stmt.execute("insert into test_table values ('FOO')");
            }

            try (Statement stmt = c2.createStatement()) {
                ResultSet rs = stmt.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='test_table'");

                assertTrue(rs.next());
            }

            try (Statement stmt = c2.createStatement()) {
                ResultSet rs = stmt.executeQuery("select * from test_table");
                assertTrue(rs.next());
                assertEquals("FOO", rs.getString("value"));
            }

            try (Statement stmt = c1.createStatement()) {
                stmt.execute("drop table test_table");
            }
        }
    }

    @Test
    void notSharedConnection() throws SQLException, IOException {
        SQLiteDriver driver = new SQLiteDriver(
            new DefaultConfiguration(new IniDriver(new Ini(new File("src/test/test_config.ini"))))
                .module(DatabaseConfiguration.MODULE)
                .connection("no_shared")
        );

        try (
            Connection c1 = driver.newConnection();
            Connection c2 = driver.newConnection()
        ) {
            try (Statement stmt = c1.createStatement()) {
                stmt.execute("create table test_table (`value` text)");
                stmt.execute("insert into test_table values ('FOO')");
            }

            try (Statement stmt = c2.createStatement()) {
                ResultSet rs = stmt.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='test_table'");

                assertFalse(rs.next());
            }

            try (Statement stmt = c1.createStatement()) {
                stmt.execute("drop table test_table");
            }
        }
    }

    @Test
    void newConnectionInFile() throws SQLException, IOException {
        SQLiteDriver driver = new SQLiteDriver(
            new DefaultConfiguration(new IniDriver(new Ini(new File("src/test/test_config.ini"))))
                .module(DatabaseConfiguration.MODULE)
                .connection("test_sqlite")
        );

        try (Connection connection = driver.newConnection()) {
            assertTrue(connection instanceof SQLiteConnection);

            try (Statement stmt = connection.createStatement()) {
                assertEquals(0, stmt.executeUpdate("create table test_table (`value` text)"));
            }
        }

        File dbFile = new File("test.db");

        assertTrue(dbFile.exists());
        dbFile.delete();
    }
}
