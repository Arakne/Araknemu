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

package fr.quatrevieux.araknemu.data.living.repository.implementation.sql;

import fr.quatrevieux.araknemu.DatabaseTestCase;
import fr.quatrevieux.araknemu.core.dbal.executor.ConnectionPoolExecutor;
import fr.quatrevieux.araknemu.data.living.entity.account.AccountBank;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class SqlAccountBankRepositoryTest extends DatabaseTestCase {
    private fr.quatrevieux.araknemu.data.living.repository.account.AccountBankRepository repository;

    @BeforeEach
    @Override
    public void setUp() throws Exception {
        super.setUp();

        repository = new SqlAccountBankRepository(new ConnectionPoolExecutor(connection));
        repository.initialize();
    }

    @AfterEach
    void tearDown() throws SQLException {
        dropTable("BANK");
    }

    @Test
    void testInitialize() throws SQLException {
        assertTableExists("BANK");
    }

    @Test
    void testAdd() {
        AccountBank bank = repository.add(new AccountBank(1, 2, 5000));

        assertEquals(1, bank.accountId());
        assertEquals(2, bank.serverId());
        assertEquals(5000, bank.kamas());
    }

    @Test
    void testAddForUpdate() {
        AccountBank bank = repository.add(new AccountBank(1, 2, 5000));

        bank.setKamas(10000);

        repository.add(bank);

        assertEquals(10000, repository.get(bank).kamas());
    }

    @Test
    void testGet() {
        repository.add(new AccountBank(1, 2, 5000));

        AccountBank bank = repository.get(new AccountBank(1, 2, 0));

        assertEquals(1, bank.accountId());
        assertEquals(2, bank.serverId());
        assertEquals(5000, bank.kamas());
    }

    @Test
    void getNotFound() {
        AccountBank bank = new AccountBank(1, 2, 0);

        assertSame(bank, repository.get(bank));
    }

    @Test
    void testHas() {
        repository.add(new AccountBank(1, 2, 5000));

        assertFalse(repository.has(new AccountBank(1, 5, 0)));
        assertTrue(repository.has(new AccountBank(1, 2, 0)));
    }

    @Test
    void testDelete() {
        AccountBank bank = repository.add(new AccountBank(1, 2, 5000));

        repository.delete(bank);

        assertFalse(repository.has(bank));
    }
}