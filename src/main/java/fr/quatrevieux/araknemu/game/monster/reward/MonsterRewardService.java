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

package fr.quatrevieux.araknemu.game.monster.reward;

import fr.quatrevieux.araknemu.data.world.entity.monster.MonsterRewardData;
import fr.quatrevieux.araknemu.data.world.entity.monster.MonsterRewardItem;
import fr.quatrevieux.araknemu.data.world.repository.monster.MonsterRewardItemRepository;
import fr.quatrevieux.araknemu.data.world.repository.monster.MonsterRewardRepository;
import fr.quatrevieux.araknemu.game.PreloadableService;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manage all monster rewards
 */
public final class MonsterRewardService implements PreloadableService {
    private final MonsterRewardRepository repository;
    private final MonsterRewardItemRepository itemRepository;

    private final Map<Integer, MonsterGradesReward> rewards = new ConcurrentHashMap<>();
    private boolean preloading = false;

    public MonsterRewardService(MonsterRewardRepository repository, MonsterRewardItemRepository itemRepository) {
        this.repository = repository;
        this.itemRepository = itemRepository;
    }

    @Override
    public void preload(Logger logger) {
        logger.info("Loading monsters rewards...");

        final Map<Integer, List<MonsterRewardItem>> itemDrops = itemRepository.all();

        for (MonsterRewardData data : repository.all()) {
            rewards.put(
                data.id(),
                new DefaultMonsterGradesReward(
                    data,
                    itemDrops.getOrDefault(data.id(), Collections.emptyList())
                )
            );
        }

        preloading = true;
        logger.info("{} monsters rewards loaded", rewards.size());
    }

    /**
     * Get the rewards for the given monster grade
     *
     * @param monsterId The monster id to load
     * @param gradeNumber The grade number (starts at 1)
     */
    public MonsterReward get(int monsterId, int gradeNumber) {
        return rewards
            .computeIfAbsent(monsterId, id -> preloading
                ? NullMonsterGradesReward.INSTANCE
                : repository.get(id)
                    .<MonsterGradesReward>map(data -> new DefaultMonsterGradesReward(data, itemRepository.byMonster(monsterId)))
                    .orElse(NullMonsterGradesReward.INSTANCE)
            )
            .grade(gradeNumber)
        ;
    }
}
