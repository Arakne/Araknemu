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

package fr.quatrevieux.araknemu.game.monster;

import fr.arakne.utils.value.Interval;
import fr.arakne.utils.value.helper.RandomUtil;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * Set of grades for a monster template
 */
public final class GradeSet {
    private static final RandomUtil RANDOM = RandomUtil.createShared();

    private final List<Monster> grades;

    GradeSet(List<Monster> grades) {
        this.grades = grades;
    }

    /**
     * Get all available grades
     * The grades are sorted by level (lower to higher level)
     */
    public List<Monster> all() {
        return grades;
    }

    /**
     * Get one grade from the GradeSet
     * @param i The grade to get from 1 to 6
     */
    public Monster get(int i) {
        return grades.get(i - 1);
    }

    /**
     * Get all grades that contained into the given level interval
     * The interval is inclusive (min <= grade level <= max)
     *
     * If the interval is larger than grade levels interval, all grades are returned
     * If the interval is disjoint with grade levels, an empty list is returned
     */
    public List<Monster> in(Interval levels) {
        // All levels are requested
        if (levels.min() == 1 && levels.max() == Integer.MAX_VALUE) {
            return grades;
        }

        return grades.stream()
            .filter(monster -> levels.contains(monster.level()))
            .collect(Collectors.toList())
        ;
    }

    /**
     * Get one random grade extracted from matching grades into levels interval (including)
     *
     * @see GradeSet#in(Interval)
     *
     * @throws NoSuchElementException When levels interval is disjoint with grades levels
     */
    public Monster random(Interval levels) {
        final List<Monster> matching = in(levels);

        switch (matching.size()) {
            case 0:
                throw new NoSuchElementException("Cannot found any valid grade for monster " + grades.get(0).id());

            case 1:
                return matching.get(0);

            default:
                return RANDOM.of(matching);
        }
    }
}
