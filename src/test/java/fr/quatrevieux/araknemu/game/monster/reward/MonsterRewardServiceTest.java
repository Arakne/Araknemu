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

package fr.quatrevieux.araknemu.game.monster.reward;

import fr.quatrevieux.araknemu.data.value.Interval;
import fr.quatrevieux.araknemu.data.world.repository.monster.MonsterRewardItemRepository;
import fr.quatrevieux.araknemu.data.world.repository.monster.MonsterRewardRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class MonsterRewardServiceTest extends GameBaseCase {
    private MonsterRewardService service;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        service = new MonsterRewardService(
            container.get(MonsterRewardRepository.class),
            container.get(MonsterRewardItemRepository.class)
        );
        dataSet.pushMonsterTemplates();
    }

    @Test
    void preload() {
        Logger logger = Mockito.mock(Logger.class);

        service.preload(logger);

        Mockito.verify(logger).info("Loading monsters rewards...");
        Mockito.verify(logger).info("{} monsters rewards loaded", 3);
    }

    @Test
    void get() {
        MonsterReward reward = service.get(31, 1);

        assertEquals(new Interval(50, 70), reward.kamas());
        assertEquals(3, reward.experience());

        reward = service.get(31, 2);
        assertEquals(new Interval(50, 70), reward.kamas());
        assertEquals(7, reward.experience());

        assertCount(1, reward.items());
        assertEquals(39, reward.items().get(0).itemTemplateId());
    }

    @Test
    void getNotFound() {
        MonsterReward reward = service.get(5, 1);

        assertEquals(new Interval(0, 0), reward.kamas());
        assertEquals(0, reward.experience());
        assertCount(0, reward.items());
    }
}
