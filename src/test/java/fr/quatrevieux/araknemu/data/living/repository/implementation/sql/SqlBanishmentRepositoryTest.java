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
import fr.quatrevieux.araknemu.data.living.entity.account.Banishment;
import fr.quatrevieux.araknemu.data.living.transformer.InstantTransformer;
import fr.quatrevieux.araknemu.realm.RealmBaseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SqlBanishmentRepositoryTest extends RealmBaseCase {
    private SqlBanishmentRepository repository;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.use(Banishment.class);

        repository = new SqlBanishmentRepository(
            new ConnectionPoolExecutor(app.database().get("realm")),
            new InstantTransformer()
        );
    }

    @Test
    void getNotFound() {
        assertThrows(EntityNotFoundException.class, () -> repository.get(new Banishment(1, 0, null, null, null, 0)));
    }

    @Test
    void addAndGet() {
        Banishment banishment = new Banishment(5, Instant.parse("2020-07-25T15:00:00.Z"), Instant.parse("2020-07-30T15:00:00.Z"), "my cause", 3);
        banishment = repository.add(banishment);

        assertEquals(1, banishment.id());

        assertEquals(5, repository.get(banishment).accountId());
        assertEquals(banishment.startDate(), repository.get(banishment).startDate());
        assertEquals(banishment.endDate(), repository.get(banishment).endDate());
        assertEquals("my cause", repository.get(banishment).cause());
        assertEquals(3, repository.get(banishment).banisherId());
    }

    @Test
    void delete() {
        Banishment banishment = repository.add(new Banishment(5, Instant.parse("2020-07-25T15:00:00.Z"), Instant.parse("2020-07-30T15:00:00.Z"), "my cause", 3));
        repository.delete(banishment);
        assertFalse(repository.has(banishment));
    }

    @Test
    void has() {
        assertFalse(repository.has(new Banishment(1, 0, null, null, null, 0)));
        Banishment banishment = repository.add(new Banishment(5, Instant.parse("2020-07-25T15:00:00.Z"), Instant.parse("2020-07-30T15:00:00.Z"), "my cause", 3));
        assertTrue(repository.has(banishment));
    }

    @Test
    void isBanned() {
        assertFalse(repository.isBanned(5));

        repository.add(new Banishment(3, Instant.now().minus(1, ChronoUnit.HOURS), Instant.now().plus(1, ChronoUnit.HOURS), "my cause", 3)); // other account
        assertFalse(repository.isBanned(5));

        repository.add(new Banishment(5, Instant.now().minus(5, ChronoUnit.HOURS), Instant.now().minus(1, ChronoUnit.HOURS), "my cause", 3)); // past
        assertFalse(repository.isBanned(5));

        repository.add(new Banishment(5, Instant.now().plus(1, ChronoUnit.HOURS), Instant.now().plus(5, ChronoUnit.HOURS), "my cause", 3)); // future
        assertFalse(repository.isBanned(5));

        repository.add(new Banishment(5, Instant.now().minus(1, ChronoUnit.HOURS), Instant.now().plus(5, ChronoUnit.HOURS), "my cause", 3)); // match
        assertTrue(repository.isBanned(5));
    }

    @Test
    void forAccount() {
        dataSet.push(new Banishment(4, Instant.parse("2020-07-25T15:00:00.Z"), Instant.parse("2020-07-30T15:00:00.Z"), "ban 1", -1));
        dataSet.push(new Banishment(4, Instant.parse("2020-07-25T16:00:00.Z"), Instant.parse("2020-07-30T15:00:00.Z"), "ban 2", 1));
        dataSet.push(new Banishment(4, Instant.parse("2020-07-25T17:00:00.Z"), Instant.parse("2020-07-30T15:00:00.Z"), "ban 3", 1));
        dataSet.push(new Banishment(4, Instant.parse("2020-07-25T18:00:00.Z"), Instant.parse("2020-07-30T15:00:00.Z"), "ban 4", 2));
        dataSet.push(new Banishment(5, Instant.parse("2020-07-25T18:00:00.Z"), Instant.parse("2020-07-30T15:00:00.Z"), "other", -1));

        List<Banishment> entries = repository.forAccount(4);
        assertCount(4, entries);

        assertEquals(4, entries.get(0).accountId());
        assertEquals("ban 4", entries.get(0).cause());
        assertEquals(2, entries.get(0).banisherId());
        assertEquals(4, entries.get(1).accountId());
        assertEquals("ban 3", entries.get(1).cause());
        assertEquals(1, entries.get(1).banisherId());
        assertEquals(4, entries.get(2).accountId());
        assertEquals("ban 2", entries.get(2).cause());
        assertEquals(1, entries.get(2).banisherId());
        assertEquals(4, entries.get(3).accountId());
        assertEquals("ban 1", entries.get(3).cause());
        assertEquals(-1, entries.get(3).banisherId());
    }

    @Test
    void removeActive() {
        dataSet.push(new Banishment(4, Instant.now().minus(1, ChronoUnit.HOURS), Instant.now().plus(1, ChronoUnit.HOURS), "cause", -1));
        dataSet.push(new Banishment(4, Instant.now().minus(1, ChronoUnit.HOURS), Instant.now().plus(1, ChronoUnit.HOURS), "cause", -1));
        dataSet.push(new Banishment(4, Instant.now().minus(2, ChronoUnit.HOURS), Instant.now().minus(1, ChronoUnit.HOURS), "old", -1));
        dataSet.push(new Banishment(5, Instant.now().minus(1, ChronoUnit.HOURS), Instant.now().plus(1, ChronoUnit.HOURS), "other", -1));

        repository.removeActive(4);

        assertCount(1, repository.forAccount(4));
        assertEquals("old", repository.forAccount(4).get(0).cause());
        assertCount(1, repository.forAccount(5));
        assertEquals("other", repository.forAccount(5).get(0).cause());
    }
}
