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
import fr.quatrevieux.araknemu.data.world.entity.monster.MonsterRewardItem;

import java.util.List;

/**
 * Base implementation for single monster grade reward
 */
final public class DefaultMonsterReward implements MonsterReward {
    final private MonsterGradesReward reward;
    final private int grade;

    public DefaultMonsterReward(MonsterGradesReward reward, int grade) {
        this.reward = reward;
        this.grade = grade;
    }

    @Override
    public Interval kamas() {
        return reward.kamas();
    }

    @Override
    public long experience() {
        return reward.experience(grade);
    }

    @Override
    public List<MonsterRewardItem> items() {
        return reward.items();
    }
}
