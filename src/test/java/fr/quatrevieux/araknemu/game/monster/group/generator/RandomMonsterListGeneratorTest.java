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

package fr.quatrevieux.araknemu.game.monster.group.generator;

import fr.arakne.utils.value.Interval;
import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.data.world.entity.monster.MonsterGroupData;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.monster.MonsterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

class RandomMonsterListGeneratorTest  extends GameBaseCase {
    private RandomMonsterListGenerator generator;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet
            .pushMonsterTemplates()
            .pushMonsterGroups()
            .pushMonsterSpells()
        ;

        generator = new RandomMonsterListGenerator(container.get(MonsterService.class));
    }

    @Test
    void groupSize1() {
        MonsterGroupData data = new MonsterGroupData(1, Duration.ZERO, 1, 1, Arrays.asList(new MonsterGroupData.Monster(31, new Interval(3, 3), 1)), "", new Position(0, 0), false);

        assertCount(1, generator.generate(data));
    }

    @Test
    void groupSize2() {
        Map<Integer, Integer> counts = countsForMaxSize(2);

        assertBetween(480, 520, counts.get(1));
        assertBetween(480, 520, counts.get(2));
    }

    @Test
    void groupSize3() {
        Map<Integer, Integer> counts = countsForMaxSize(3);

        assertBetween(320, 345, counts.get(1));
        assertBetween(320, 345, counts.get(2));
        assertBetween(320, 345, counts.get(3));
    }

    @Test
    void groupSize4() {
        Map<Integer, Integer> counts = countsForMaxSize(4);

        assertBetween(210, 230, counts.get(1));
        assertBetween(250, 270, counts.get(2));
        assertBetween(250, 270, counts.get(3));
        assertBetween(250, 270, counts.get(4));
    }

    @Test
    void groupSize5() {
        Map<Integer, Integer> counts = countsForMaxSize(5);

        assertBetween(140, 170, counts.get(1));
        assertBetween(190, 210, counts.get(2));
        assertBetween(240, 260, counts.get(3));
        assertBetween(240, 260, counts.get(4));
        assertBetween(140, 160, counts.get(5));
    }

    @Test
    void groupSize6() {
        Map<Integer, Integer> counts = countsForMaxSize(6);

        assertBetween(90, 110, counts.get(1));
        assertBetween(140, 160, counts.get(2));
        assertBetween(190, 215, counts.get(3));
        assertBetween(190, 215, counts.get(4));
        assertBetween(190, 215, counts.get(5));
        assertBetween(140, 160, counts.get(6));
    }

    @Test
    void groupSize7() {
        Map<Integer, Integer> counts = countsForMaxSize(7);

        assertBetween(80, 105, counts.get(1));
        assertBetween(100, 120, counts.get(2));
        assertBetween(120, 160, counts.get(3));
        assertBetween(190, 215, counts.get(4));
        assertBetween(190, 215, counts.get(5));
        assertBetween(150, 175, counts.get(6));
        assertBetween(80, 100, counts.get(7));
    }

    @Test
    void groupSize8() {
        Map<Integer, Integer> counts = countsForMaxSize(8);

        assertBetween(80, 100, counts.get(1));
        assertBetween(100, 120, counts.get(2));
        assertBetween(120, 140, counts.get(3));
        assertBetween(160, 180, counts.get(4));
        assertBetween(160, 180, counts.get(5));
        assertBetween(120, 140, counts.get(6));
        assertBetween(100, 125, counts.get(7));
        assertBetween(80, 100, counts.get(8));
    }

    @Test
    void rate() {
        MonsterGroupData data = new MonsterGroupData(1, Duration.ZERO, 1, 1, Arrays.asList(new MonsterGroupData.Monster(31, new Interval(0, 100), 2), new MonsterGroupData.Monster(34, new Interval(0, 100), 1)), "", new Position(0, 0), false);

        Map<Integer, Integer> idsCounts = new HashMap<>();

        for (int i = 0; i < 100; ++i) {
            int id = generator.generate(data).get(0).id();

            idsCounts.put(id, idsCounts.getOrDefault(id, 0) + 1);
        }

        assertBetween(56, 76, idsCounts.get(31));
        assertBetween(23, 43, idsCounts.get(34));
    }

    private Map<Integer, Integer> countsForMaxSize(int maxSize) {
        MonsterGroupData data = new MonsterGroupData(1, Duration.ZERO, maxSize, 1, Arrays.asList(new MonsterGroupData.Monster(31, new Interval(3, 3), 1)), "", new Position(0, 0), false);

        Map<Integer, Integer> counts = new HashMap<>();

        for (int i = 0; i < 1000; ++i) {
            int count = generator.generate(data).size();

            counts.put(count, counts.getOrDefault(count, 0) + 1);
        }

        return counts;
    }
}
