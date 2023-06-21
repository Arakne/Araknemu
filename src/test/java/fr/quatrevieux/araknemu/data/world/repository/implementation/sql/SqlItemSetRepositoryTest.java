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

package fr.quatrevieux.araknemu.data.world.repository.implementation.sql;

import fr.quatrevieux.araknemu.core.dbal.executor.ConnectionPoolExecutor;
import fr.quatrevieux.araknemu.core.dbal.repository.EntityNotFoundException;
import fr.quatrevieux.araknemu.data.world.entity.item.ItemSet;
import fr.quatrevieux.araknemu.data.world.transformer.ItemSetBonusTransformer;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SqlItemSetRepositoryTest extends GameBaseCase {
    private SqlItemSetRepository repository;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        repository = new SqlItemSetRepository(
            new ConnectionPoolExecutor(app.database().get("game")),
            new ItemSetBonusTransformer()
        );

        dataSet.pushItemSets();
    }

    @Test
    void getNotFound() {
        assertThrows(EntityNotFoundException.class, () -> repository.get(-5));
    }

    @Test
    void getById() {
        ItemSet itemSet = repository.get(1);

        assertEquals(1, itemSet.id());
        assertEquals("Panoplie du Bouftou", itemSet.name());
        assertCount(6, itemSet.bonus());
    }

    @Test
    void getByEntity() {
        ItemSet itemSet = repository.get(new ItemSet(1, null, null));

        assertEquals(1, itemSet.id());
        assertEquals("Panoplie du Bouftou", itemSet.name());
        assertCount(6, itemSet.bonus());
    }

    @Test
    void has() {
        assertTrue(repository.has(new ItemSet(1, null, null)));
        assertFalse(repository.has(new ItemSet(-5, null, null)));
    }

    @Test
    void load() {
        Collection<ItemSet> sets = repository.load();

        assertCount(3, sets);
    }
}
