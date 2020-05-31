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

package fr.quatrevieux.araknemu.core.dbal.repository;

import fr.quatrevieux.araknemu.core.config.DefaultConfiguration;
import fr.quatrevieux.araknemu.core.config.IniDriver;
import fr.quatrevieux.araknemu.core.dbal.DatabaseConfiguration;
import fr.quatrevieux.araknemu.core.dbal.DefaultDatabaseHandler;
import fr.quatrevieux.araknemu.core.dbal.executor.ConnectionPoolExecutor;
import org.apache.logging.log4j.LogManager;
import org.ini4j.Ini;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RepositoryUtilsTest {
    public static class Person {
        public int id;
        public String firstName;
        public String lastName;
        public int age;
    }

    public static class PersonLoader implements RepositoryUtils.Loader<Person> {
        @Override
        public Person create(ResultSet rs) throws SQLException {
            Person p = new Person();

            p.id = rs.getInt("ID");
            p.firstName = rs.getString("FIRST_NAME");
            p.lastName = rs.getString("LAST_NAME");
            p.age = rs.getInt("AGE");

            return p;
        }

        @Override
        public Person fillKeys(Person entity, ResultSet keys) throws SQLException {
            entity.id = keys.getInt(1);

            return entity;
        }
    }

    private RepositoryUtils<Person> utils;
    private ConnectionPoolExecutor pool;

    @BeforeEach
    void setUp() throws IOException, SQLException {
        pool = new ConnectionPoolExecutor(
            new DefaultDatabaseHandler(
                new DefaultConfiguration(
                    new IniDriver(
                        new Ini(new File("src/test/test_config.ini"))
                    )
                ).module(DatabaseConfiguration.class),
                LogManager.getLogger()
            ).get("realm")
        );

        utils = new RepositoryUtils<>(pool, new PersonLoader());

        pool.query("CREATE TABLE PERSON (ID INTEGER PRIMARY KEY AUTOINCREMENT, FIRST_NAME TEXT, LAST_NAME TEXT, AGE INTEGER)");

        pool.query("INSERT INTO PERSON VALUES (1, 'JOHN', 'DOE', 23), (2, 'ALAN', 'SMITH', 45), (3, 'JEAN', 'DUPONT', 32)");
    }

    @AfterEach
    void tearDown() throws SQLException {
        pool.query("DROP TABLE PERSON");
    }

    @Test
    void findOneFound() throws RepositoryException {
        Person p = utils.findOne("SELECT * FROM PERSON WHERE ID = ?", rs -> rs.setInt(1, 1));

        assertEquals(1, p.id);
        assertEquals("JOHN", p.firstName);
        assertEquals("DOE", p.lastName);
        assertEquals(23, p.age);
    }

    @Test
    void findOneNotFound() {
        assertThrows(
            EntityNotFoundException.class,
            () -> utils.findOne("SELECT * FROM PERSON WHERE ID = ?", rs -> rs.setInt(1, 15))
        );
    }

    @Test
    void findOneBaqQuery() {
        assertThrows(
            RepositoryException.class,
            () -> utils.findOne("bad sql", rs -> {})
        );
    }

    @Test
    void findAll() throws RepositoryException {
        List<Person> people = utils.findAll(
            "SELECT * FROM PERSON WHERE FIRST_NAME LIKE ?",
            rs -> rs.setString(1, "%A%")
        );

        assertEquals(2, people.size());
        assertEquals("ALAN", people.get(0).firstName);
        assertEquals("JEAN", people.get(1).firstName);
    }

    @Test
    void findAllBaqQuery() {
        assertThrows(
            RepositoryException.class,
            () -> utils.findAll("bad sql", rs -> {})
        );
    }

    @Test
    void aggregate() throws RepositoryException {
        assertEquals(2, utils.aggregate(
            "SELECT COUNT(*) FROM PERSON WHERE FIRST_NAME LIKE ?",
            rs -> rs.setString(1, "%A%")
        ));
    }

    @Test
    void simpleUpdate() throws RepositoryException {
        assertEquals(2, utils.update(
            "UPDATE PERSON SET AGE = ? WHERE ID > ?",
            rs -> {
                rs.setInt(1, 63);
                rs.setInt(2, 1);
            }
        ));

        assertEquals(63, utils.findOne("SELECT * FROM PERSON WHERE ID = ?", rs -> rs.setInt(1, 2)).age);
        assertEquals(63, utils.findOne("SELECT * FROM PERSON WHERE ID = ?", rs -> rs.setInt(1, 3)).age);
    }

    @Test
    void insertUpdate() throws RepositoryException {
        Person p = new Person();
        p.age = 41;
        p.firstName = "PAUL";
        p.lastName = "DOUDOU";

        assertSame(p, utils.update(
            "INSERT INTO PERSON (FIRST_NAME, LAST_NAME, AGE) VALUES (?, ?, ?)",
            rs -> {
                rs.setString(1, p.firstName);
                rs.setString(2, p.lastName);
                rs.setInt(3, p.age);
            },
            p
        ));

        assertEquals(4, p.id);
    }
}