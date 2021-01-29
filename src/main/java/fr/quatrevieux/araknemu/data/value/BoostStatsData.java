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

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Data for boost stats per class
 */
final public class BoostStatsData {
    final static public class Interval {
        final private int start;
        final private int cost;
        final private int boost;

        public Interval(int start, int cost, int boost) {
            this.start = start;
            this.cost = cost;
            this.boost = boost;
        }

        /**
         * The start characteristics points interval for the current boost rule
         * This value is inclusive
         */
        public int start() {
            return start;
        }

        /**
         * The cost for boot the characteristic
         */
        public int cost() {
            return cost;
        }

        /**
         * The boost value : how many characteristics points are added on boost
         */
        public int boost() {
            return boost;
        }

        @Override
        public String toString() {
            return "[" + start + ", " + cost + ", " + boost + "]";
        }
    }

    final private Map<Characteristic, List<Interval>> characteristics;

    /**
     * BoostStatsData constructor
     *
     * @param characteristics Boosts intervals indexed by characteristic. The list of interval must be sorted by "start" asc
     */
    public BoostStatsData(Map<Characteristic, List<Interval>> characteristics) {
        this.characteristics = characteristics;
    }

    /**
     * Get the boost interval
     *
     * @param characteristic The characteristic to boot
     * @param value The current characteristic value
     *
     * @throws NoSuchElementException If the characteristic cannot be boosted
     * @throws RuntimeException When the intervals are not valid
     */
    public Interval get(Characteristic characteristic, int value) {
        if (!characteristics.containsKey(characteristic)) {
            throw new NoSuchElementException("Cannot boost " + characteristic);
        }

        if (value < 0) {
            throw new IllegalArgumentException("The stats value must be positive");
        }

        List<Interval> intervals = characteristics.get(characteristic);

        for (int i = intervals.size() - 1; i >= 0; --i) {
            Interval interval = intervals.get(i);

            if (interval.start <= value) {
                return interval;
            }
        }

        throw new RuntimeException("Invalid boost interval for " + characteristic);
    }
}
