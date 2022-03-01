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

import fr.quatrevieux.araknemu.data.transformer.Transformer;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.Characteristics;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.PolyNull;

import java.util.Collections;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 *
 */
public final class RaceBaseStatsTransformer implements Transformer<SortedMap<Integer, Characteristics>> {
    private final Transformer<Characteristics> characteristicsTransformer;

    public RaceBaseStatsTransformer(Transformer<Characteristics> characteristicsTransformer) {
        this.characteristicsTransformer = characteristicsTransformer;
    }

    @Override
    public @PolyNull String serialize(@PolyNull SortedMap<Integer, Characteristics> value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public @NonNull SortedMap<Integer, Characteristics> unserialize(@PolyNull String serialize) {
        if (serialize == null || serialize.isEmpty()) {
            throw new IllegalArgumentException("Race stats cannot be empty");
        }

        final SortedMap<Integer, Characteristics> stats = new TreeMap<>(Collections.reverseOrder());

        for (String levelStats : StringUtils.split(serialize, "|")) {
            final String[] parts = StringUtils.split(levelStats, "@", 2);

            stats.put(
                Integer.parseUnsignedInt(parts[1]),
                characteristicsTransformer.unserialize(parts[0])
            );
        }

        return stats;
    }
}
