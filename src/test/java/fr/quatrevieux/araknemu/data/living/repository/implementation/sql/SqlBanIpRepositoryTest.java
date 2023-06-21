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
import fr.quatrevieux.araknemu.data.living.entity.BanIp;
import fr.quatrevieux.araknemu.data.living.transformer.InstantTransformer;
import fr.quatrevieux.araknemu.data.living.transformer.IpAddressTransformer;
import fr.quatrevieux.araknemu.realm.RealmBaseCase;
import inet.ipaddr.IPAddressString;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SqlBanIpRepositoryTest extends RealmBaseCase {
    private SqlBanIpRepository repository;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.use(BanIp.class);

        repository = new SqlBanIpRepository(
            new ConnectionPoolExecutor(app.database().get("realm")),
            new IpAddressTransformer(),
            new InstantTransformer()
        );
    }

    @Test
    void getNotFound() {
        assertThrows(EntityNotFoundException.class, () -> repository.get(new BanIp(1, null, null, null, null, 0)));
    }

    @Test
    void addAndGet() {
        BanIp entity = new BanIp(new IPAddressString("156.32.47.0/24"), Instant.parse("2020-07-30T15:00:00.Z"), Instant.parse("2020-07-30T15:00:00.Z"), "my cause", 3);
        entity = repository.add(entity);

        assertEquals(1, entity.id());

        assertEquals(new IPAddressString("156.32.47.0/24"), repository.get(entity).ipAddress());
        assertEquals(entity.expiresAt(), repository.get(entity).expiresAt());
        assertEquals("my cause", repository.get(entity).cause());
        assertEquals(3, repository.get(entity).banisherId());
    }

    @Test
    void delete() {
        BanIp entity = repository.add(new BanIp(new IPAddressString("156.32.47.0/24"), Instant.parse("2020-07-30T15:00:00.Z"), Instant.parse("2020-07-30T15:00:00.Z"), "my cause", 3));
        repository.delete(entity);
        assertFalse(repository.has(entity));
    }

    @Test
    void has() {
        assertFalse(repository.has(new BanIp(1, null, null, null, null, 0)));
        BanIp entity = repository.add(new BanIp(new IPAddressString("156.32.47.0/24"), Instant.parse("2020-07-30T15:00:00.Z"), Instant.parse("2020-07-30T15:00:00.Z"), "my cause", 3));
        assertTrue(repository.has(entity));
    }

    @Test
    void available() {
        dataSet.push(new BanIp(new IPAddressString("156.32.47.0/24"), Instant.now(), Instant.now().minus(1, ChronoUnit.HOURS), "ban 1", -1));
        dataSet.push(new BanIp(new IPAddressString("125.112.32.56"), Instant.now(), Instant.now().plus(1, ChronoUnit.HOURS), "ban 2", 1));
        dataSet.push(new BanIp(new IPAddressString("32.0.0.0/8"), Instant.now(), null, "ban 3", 1));

        List<BanIp> entries = new ArrayList<>(repository.available());
        assertCount(2, entries);

        assertEquals(new IPAddressString("125.112.32.56"), entries.get(0).ipAddress());
        assertEquals(new IPAddressString("32.0.0.0/8"), entries.get(1).ipAddress());
    }

    @Test
    void updated() {
        Instant now = Instant.now();

        dataSet.push(new BanIp(new IPAddressString("156.32.47.0/24"), now.minus(10, ChronoUnit.SECONDS), now.minus(1, ChronoUnit.HOURS), "ban 1", -1));
        dataSet.push(new BanIp(new IPAddressString("125.112.32.56"), now, now.plus(1, ChronoUnit.HOURS), "ban 2", 1));
        dataSet.push(new BanIp(new IPAddressString("32.0.0.0/8"), now.plus(10, ChronoUnit.SECONDS), null, "ban 3", 1));

        List<BanIp> entries = new ArrayList<>(repository.updated(now));
        assertCount(2, entries);

        assertEquals(new IPAddressString("125.112.32.56"), entries.get(0).ipAddress());
        assertEquals(new IPAddressString("32.0.0.0/8"), entries.get(1).ipAddress());
    }

    @Test
    void disable() {
        Instant now = Instant.now();

        BanIp banIp1 = dataSet.push(new BanIp(new IPAddressString("156.32.47.24"), now, now.plus(1, ChronoUnit.HOURS), "ban 1", -1));
        BanIp banIp2 = dataSet.push(new BanIp(new IPAddressString("156.32.47.25"), now, null, "ban 1", -1));
        BanIp banIp3 = dataSet.push(new BanIp(new IPAddressString("156.32.47.26"), now, now.minus(1, ChronoUnit.HOURS).with(ChronoField.MICRO_OF_SECOND, 0), "ban 1", -1));

        repository.disable(new IPAddressString("156.32.47.24"));

        assertFalse(repository.get(banIp1).active());
        assertBetween(-1, 1, repository.get(banIp1).expiresAt().get().getEpochSecond() - now.getEpochSecond());
        assertTrue(repository.get(banIp2).active());
        assertFalse(repository.get(banIp3).active());
        assertEquals(banIp3.expiresAt(), repository.get(banIp3).expiresAt());

        repository.disable(new IPAddressString("156.32.47.25"));
        assertBetween(-1, 1, repository.get(banIp2).expiresAt().get().getEpochSecond() - now.getEpochSecond());
    }
}
