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
 * Store rewards for all monster grades
 */
public interface MonsterGradesReward {
    /**
     * The win kamas interface
     */
    public Interval kamas();

    /**
     * List of dropped items
     */
    public List<MonsterRewardItem> items();

    /**
     * Get the base experience for the given grade
     *
     * @param gradeNumber The monster grade number (starts at 1)
     *
     * @see fr.quatrevieux.araknemu.game.monster.Monster#gradeNumber()
     */
    public long experience(int gradeNumber);

    /**
     * Creates the reward for the given monster grade
     *
     * @param gradeNumber The monster grade number (starts at 1)
     *
     * @see fr.quatrevieux.araknemu.game.monster.Monster#gradeNumber()
     */
    public MonsterReward grade(int gradeNumber);
}
