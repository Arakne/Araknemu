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

package fr.quatrevieux.araknemu.data.value;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BoostStatsDataTest {
    @Test
    void getBadCharacteristic() {
        BoostStatsData boost = new BoostStatsData(new HashMap<>());

        assertThrows(NoSuchElementException.class, () -> boost.get(Characteristic.MAX_SUMMONED_CREATURES, 10));
    }

    @Test
    void getNegativeValue() {
        Map<Characteristic, List<BoostStatsData.Interval>> map = new HashMap<>();

        map.put(
            Characteristic.INTELLIGENCE,
            Arrays.asList(
                new BoostStatsData.Interval(0, 1, 1),
                new BoostStatsData.Interval(50, 2, 1),
                new BoostStatsData.Interval(100, 3, 1),
                new BoostStatsData.Interval(150, 4, 1),
                new BoostStatsData.Interval(200, 5, 1)
            )
        );

        BoostStatsData boost = new BoostStatsData(map);

        assertThrows(IllegalArgumentException.class, () -> boost.get(Characteristic.INTELLIGENCE, -10));
    }

    @Test
    void getWithZeroValue() {
        Map<Characteristic, List<BoostStatsData.Interval>> map = new HashMap<>();

        map.put(
            Characteristic.INTELLIGENCE,
            Arrays.asList(
                new BoostStatsData.Interval(0, 1, 1),
                new BoostStatsData.Interval(50, 2, 1),
                new BoostStatsData.Interval(100, 3, 1),
                new BoostStatsData.Interval(150, 4, 1),
                new BoostStatsData.Interval(200, 5, 1)
            )
        );

        BoostStatsData boost = new BoostStatsData(map);

        assertEquals(1, boost.get(Characteristic.INTELLIGENCE, 0).cost());
    }

    @Test
    void getMoreMaxValue() {
        Map<Characteristic, List<BoostStatsData.Interval>> map = new HashMap<>();

        map.put(
            Characteristic.INTELLIGENCE,
            Arrays.asList(
                new BoostStatsData.Interval(0, 1, 1),
                new BoostStatsData.Interval(50, 2, 1),
                new BoostStatsData.Interval(100, 3, 1),
                new BoostStatsData.Interval(150, 4, 1),
                new BoostStatsData.Interval(200, 5, 1)
            )
        );

        BoostStatsData boost = new BoostStatsData(map);

        assertEquals(5, boost.get(Characteristic.INTELLIGENCE, 300).cost());
    }

    @Test
    void getIntervalStart() {
        Map<Characteristic, List<BoostStatsData.Interval>> map = new HashMap<>();

        map.put(
            Characteristic.INTELLIGENCE,
            Arrays.asList(
                new BoostStatsData.Interval(0, 1, 1),
                new BoostStatsData.Interval(50, 2, 1),
                new BoostStatsData.Interval(100, 3, 1),
                new BoostStatsData.Interval(150, 4, 1),
                new BoostStatsData.Interval(200, 5, 1)
            )
        );

        BoostStatsData boost = new BoostStatsData(map);

        assertEquals(2, boost.get(Characteristic.INTELLIGENCE, 50).cost());
    }

    @Test
    void getInvalidInterval() {
        Map<Characteristic, List<BoostStatsData.Interval>> map = new HashMap<>();

        map.put(
            Characteristic.INTELLIGENCE,
            Arrays.asList(
                new BoostStatsData.Interval(100, 3, 1),
                new BoostStatsData.Interval(150, 4, 1),
                new BoostStatsData.Interval(200, 5, 1)
            )
        );

        BoostStatsData boost = new BoostStatsData(map);

        assertThrows(RuntimeException.class, () -> boost.get(Characteristic.INTELLIGENCE, 50).cost());
    }
}
