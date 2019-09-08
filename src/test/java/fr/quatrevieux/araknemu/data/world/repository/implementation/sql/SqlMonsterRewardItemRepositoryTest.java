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
import fr.quatrevieux.araknemu.data.world.entity.monster.MonsterRewardItem;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SqlMonsterRewardItemRepositoryTest extends GameBaseCase {
    private SqlMonsterRewardItemRepository repository;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMonsterTemplates();

        repository = new SqlMonsterRewardItemRepository(new ConnectionPoolExecutor(app.database().get("game")));
    }

    @Test
    void notImplemented() {
        assertThrows(UnsupportedOperationException.class, () -> repository.get(new MonsterRewardItem(0, 0, 0, 0, 0)));
        assertThrows(UnsupportedOperationException.class, () -> repository.has(new MonsterRewardItem(0, 0, 0, 0, 0)));
    }

    @Test
    void byMonster() {
        List<MonsterRewardItem> items = repository.byMonster(36);

        assertCount(3, items);

        assertEquals(36, items.get(0).monsterId());
        assertEquals(2416, items.get(0).itemTemplateId());
        assertEquals(2, items.get(0).quantity());
        assertEquals(100, items.get(0).discernment());
        assertEquals(1d, items.get(0).rate());
    }

    @Test
    void byMonsterNotFound() {
        assertCount(0, repository.byMonster(-5));
    }

    @Test
    void all() {
        Map<Integer, List<MonsterRewardItem>> rewards = repository.all();

        assertCollectionEquals(rewards.keySet(), 31, 34, 36);

        assertCount(1, rewards.get(31));
        assertCount(1, rewards.get(34));
        assertCount(3, rewards.get(36));
    }
}
