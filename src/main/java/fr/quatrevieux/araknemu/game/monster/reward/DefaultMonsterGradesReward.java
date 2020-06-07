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

import fr.arakne.utils.value.Interval;
import fr.quatrevieux.araknemu.data.world.entity.monster.MonsterRewardData;
import fr.quatrevieux.araknemu.data.world.entity.monster.MonsterRewardItem;

import java.util.List;

/**
 * Base implementation for grade set rewards
 */
final public class DefaultMonsterGradesReward implements MonsterGradesReward {
    final private MonsterRewardData data;
    final private List<MonsterRewardItem> items;

    public DefaultMonsterGradesReward(MonsterRewardData data, List<MonsterRewardItem> items) {
        this.data = data;
        this.items = items;
    }

    @Override
    public Interval kamas() {
        return data.kamas();
    }

    @Override
    public long experience(int gradeNumber) {
        return data.experiences()[gradeNumber - 1];
    }

    @Override
    public List<MonsterRewardItem> items() {
        return items;
    }

    @Override
    public MonsterReward grade(int gradeNumber) {
        return new DefaultMonsterReward(this, gradeNumber);
    }
}
