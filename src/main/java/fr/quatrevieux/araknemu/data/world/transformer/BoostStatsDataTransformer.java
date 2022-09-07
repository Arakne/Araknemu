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

package fr.quatrevieux.araknemu.data.world.transformer;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.data.transformer.Transformer;
import fr.quatrevieux.araknemu.data.value.BoostStatsData;
import fr.quatrevieux.araknemu.util.Splitter;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.PolyNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Transformer for {@link BoostStatsData}
 */
public final class BoostStatsDataTransformer implements Transformer<BoostStatsData> {
    @Override
    public @PolyNull String serialize(@PolyNull BoostStatsData value) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public @NonNull BoostStatsData unserialize(@PolyNull String serialize) {
        if (serialize == null || serialize.isEmpty()) {
            throw new IllegalArgumentException("Boost stats cannot be empty");
        }

        final Map<Characteristic, List<BoostStatsData.Interval>> data = new EnumMap<>(Characteristic.class);

        for (String characteristicData : StringUtils.split(serialize, ";")) {
            final Splitter entry = new Splitter(characteristicData, ':');

            final Characteristic characteristic = Characteristic.fromId(entry.nextInt());
            final List<BoostStatsData.Interval> intervals = new ArrayList<>();

            final Splitter intervalData = entry.nextSplit(',');

            while (intervalData.hasNext()) {
                final Splitter parts = intervalData.nextSplit('@');

                intervals.add(
                    new BoostStatsData.Interval(
                        parts.nextNonNegativeInt(),
                        parts.nextPositiveInt(),
                        parts.nextPositiveIntOrDefault(1)
                    )
                );
            }

            intervals.sort(Comparator.comparingInt(BoostStatsData.Interval::start));

            data.put(characteristic, intervals);
        }

        return new BoostStatsData(data);
    }
}
