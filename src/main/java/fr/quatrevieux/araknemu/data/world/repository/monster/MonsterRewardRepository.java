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

package fr.quatrevieux.araknemu.data.world.repository.monster;

import fr.quatrevieux.araknemu.core.dbal.repository.Repository;
import fr.quatrevieux.araknemu.data.world.entity.monster.MonsterRewardData;

import java.util.List;
import java.util.Optional;

/**
 * Repository for {@link MonsterRewardData}
 */
public interface MonsterRewardRepository extends Repository<MonsterRewardData> {
    /**
     * Get reward for one monster by the monster id
     */
    public Optional<MonsterRewardData> get(int id);

    /**
     * Load all monster rewards
     */
    public List<MonsterRewardData> all();
}
