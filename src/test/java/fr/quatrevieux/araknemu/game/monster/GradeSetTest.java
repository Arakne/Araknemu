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

package fr.quatrevieux.araknemu.game.monster;

import fr.arakne.utils.value.Interval;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GradeSetTest extends GameBaseCase {
    private GradeSet gradeSet;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet
            .pushMonsterSpells()
            .pushMonsterTemplates()
        ;

        gradeSet = container.get(MonsterService.class).load(31);
    }

    @Test
    void all() {
        assertCount(5, gradeSet.all());
        assertArrayEquals(
            new int[] {2, 3, 4, 5, 6},
            gradeSet.all().stream().mapToInt(Monster::level).toArray()
        );

        assertSame(gradeSet.all(), gradeSet.all());
    }

    @Test
    void inDefaultInterval() {
        assertSame(gradeSet.all(), gradeSet.in(new Interval(1, Integer.MAX_VALUE)));
    }

    @Test
    void in() {
        assertEquals(gradeSet.all(), gradeSet.in(new Interval(1, 50)));
        assertEquals(Collections.emptyList(), gradeSet.in(new Interval(1, 1)));
        assertEquals(Collections.emptyList(), gradeSet.in(new Interval(50, 100)));
        assertCollectionEquals(gradeSet.in(new Interval(3, 3)), gradeSet.all().get(1));
        assertCollectionEquals(gradeSet.in(new Interval(3, 5)), gradeSet.all().get(1), gradeSet.all().get(2), gradeSet.all().get(3));
    }

    @Test
    void randomInvalidInterval() {
        assertThrows(NoSuchElementException.class, () -> gradeSet.random(new Interval(50, 100)));
    }

    @Test
    void randomOnlyOneMatch() {
        assertEquals(gradeSet.all().get(4), gradeSet.random(new Interval(6, 10)));
    }

    @Test
    void random() {
        Map<Integer, Integer> levels = new HashMap<>();

        for (int i = 0; i < 100; ++i) {
            int level = gradeSet.random(new Interval(3, 5)).level();

            levels.put(level, levels.getOrDefault(level, 0) + 1);
        }

        assertBetween(25, 37, levels.get(3));
        assertBetween(25, 37, levels.get(4));
        assertBetween(25, 37, levels.get(5));
    }
}
