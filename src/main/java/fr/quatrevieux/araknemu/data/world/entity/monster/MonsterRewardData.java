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

package fr.quatrevieux.araknemu.data.world.entity.monster;

import fr.arakne.utils.value.Interval;
import org.checkerframework.checker.index.qual.NonNegative;

/**
 * Store Pvm fight reward for each monsters
 *
 * Note: a monster can have no rewards
 */
public final class MonsterRewardData {
    private final int id;
    private final Interval kamas;
    private final @NonNegative long[] experiences;

    public MonsterRewardData(int id, Interval kamas, @NonNegative long[] experiences) {
        this.id = id;
        this.kamas = kamas;
        this.experiences = experiences;
    }

    /**
     * The monster template id (primary key)
     *
     * @see MonsterTemplate#id()
     */
    public int id() {
        return id;
    }

    /**
     * Interval of win kamas
     */
    public Interval kamas() {
        return kamas;
    }

    /**
     * Reward experience per grade
     */
    public @NonNegative long[] experiences() {
        return experiences;
    }
}
