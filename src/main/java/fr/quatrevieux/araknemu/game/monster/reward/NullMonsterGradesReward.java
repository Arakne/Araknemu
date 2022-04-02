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
import org.checkerframework.checker.index.qual.NonNegative;

import java.util.Collections;
import java.util.List;

/**
 * Null object for rewards
 */
public final class NullMonsterGradesReward implements MonsterGradesReward {
    public static final NullMonsterGradesReward INSTANCE = new NullMonsterGradesReward();

    private static final Interval NULL_INTERVAL = new Interval(0, 0);
    private static final MonsterReward NULL_REWARD = new MonsterReward() {
        @Override
        public Interval kamas() {
            return NULL_INTERVAL;
        }

        @Override
        public @NonNegative long experience() {
            return 0;
        }

        @Override
        public List<MonsterRewardItem> items() {
            return Collections.emptyList();
        }
    };

    @Override
    public Interval kamas() {
        return NULL_INTERVAL;
    }

    @Override
    public @NonNegative long experience(int gradeNumber) {
        return 0;
    }

    @Override
    public List<MonsterRewardItem> items() {
        return Collections.emptyList();
    }

    @Override
    public MonsterReward grade(int gradeNumber) {
        return NULL_REWARD;
    }
}
