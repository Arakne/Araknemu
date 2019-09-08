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

import fr.quatrevieux.araknemu.core.dbal.repository.EntityNotFoundException;
import fr.quatrevieux.araknemu.core.dbal.executor.ConnectionPoolExecutor;
import fr.quatrevieux.araknemu.data.constant.Effect;
import fr.quatrevieux.araknemu.data.living.entity.account.AccountBank;
import fr.quatrevieux.araknemu.data.living.entity.account.BankItem;
import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;
import fr.quatrevieux.araknemu.data.world.transformer.ItemEffectsTransformer;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class SqlBankItemRepositoryTest extends GameBaseCase {
    private SqlBankItemRepository repository;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.use(BankItem.class);

        repository = new SqlBankItemRepository(
            new ConnectionPoolExecutor(app.database().get("game")),
            new ItemEffectsTransformer()
        );
    }

    @Test
    void getNotFound() {
        assertThrows(EntityNotFoundException.class, () -> repository.get(new BankItem(123, 2, 123, 0, null, 0)));
    }

    @Test
    void addAndGet() {
        BankItem item = repository.add(
            new BankItem(
                1,
                2,
                3,
                39,
                Arrays.asList(new ItemTemplateEffectEntry(Effect.ADD_INTELLIGENCE, 2, 0, 0, "")),
                5
            )
        );

        item = repository.get(item);

        assertEquals(1, item.accountId());
        assertEquals(2, item.serverId());
        assertEquals(3, item.entryId());
        assertEquals(39, item.itemTemplateId());
        assertEquals(5, item.quantity());
    }

    @Test
    void has() {
        BankItem item = repository.add(
            new BankItem(
                1,
                2,
                3,
                39,
                Arrays.asList(new ItemTemplateEffectEntry(Effect.ADD_INTELLIGENCE, 2, 0, 0, "")),
                5
            )
        );

        assertTrue(repository.has(item));
        assertFalse(repository.has(new BankItem(0, 0, 0, 0, null, 0)));
    }

    @Test
    void update() {
        BankItem item = repository.add(
            new BankItem(
                1,
                2,
                3,
                39,
                Arrays.asList(new ItemTemplateEffectEntry(Effect.ADD_INTELLIGENCE, 2, 0, 0, "")),
                5
            )
        );

        item.setQuantity(1);

        repository.update(item);

        item = repository.get(item);

        assertEquals(1, item.accountId());
        assertEquals(2, item.serverId());
        assertEquals(3, item.entryId());
        assertEquals(39, item.itemTemplateId());
        assertEquals(1, item.quantity());
    }

    @Test
    void updateNotFound() {
        assertThrows(EntityNotFoundException.class, () -> repository.update(
            new BankItem(
                1,
                2,
                3,
                39,
                Arrays.asList(new ItemTemplateEffectEntry(Effect.ADD_INTELLIGENCE, 2, 0, 0, "")),
                5
            )
        ));
    }

    @Test
    void delete() {
        BankItem item = repository.add(
            new BankItem(
                1,
                2,
                3,
                39,
                Arrays.asList(new ItemTemplateEffectEntry(Effect.ADD_INTELLIGENCE, 2, 0, 0, "")),
                5
            )
        );

        repository.delete(item);

        assertFalse(repository.has(item));
    }

    @Test
    void deleteNotFound() {
        assertThrows(EntityNotFoundException.class, () -> repository.delete(
            new BankItem(
                1,
                2,
                3,
                39,
                Arrays.asList(new ItemTemplateEffectEntry(Effect.ADD_INTELLIGENCE, 2, 0, 0, "")),
                5
            )
        ));
    }

    @Test
    void count() {
        AccountBank bank = new AccountBank(1, 2, 0);

        assertEquals(0, repository.count(bank));

        repository.add(new BankItem(1, 2, 3, 39, Arrays.asList(), 5));
        repository.add(new BankItem(1, 2, 4, 40, Arrays.asList(), 2));

        assertEquals(2, repository.count(bank));
    }
}
