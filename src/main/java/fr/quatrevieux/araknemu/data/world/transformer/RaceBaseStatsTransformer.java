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
import fr.quatrevieux.araknemu.util.Splitter;
import org.checkerframework.checker.index.qual.Positive;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.PolyNull;

import java.util.Collections;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 *
 */
public final class RaceBaseStatsTransformer implements Transformer<SortedMap<@Positive Integer, Characteristics>> {
    private final Transformer<Characteristics> characteristicsTransformer;

    public RaceBaseStatsTransformer(Transformer<Characteristics> characteristicsTransformer) {
        this.characteristicsTransformer = characteristicsTransformer;
    }

    @Override
    public @PolyNull String serialize(@PolyNull SortedMap<@Positive Integer, Characteristics> value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public @NonNull SortedMap<@Positive Integer, Characteristics> unserialize(@PolyNull String serialize) {
        if (serialize == null || serialize.isEmpty()) {
            throw new IllegalArgumentException("Race stats cannot be empty");
        }

        final SortedMap<@Positive Integer, Characteristics> stats = new TreeMap<>(Collections.reverseOrder());
        final Splitter splitter = new Splitter(serialize, '|');

        while (splitter.hasNext()) {
            final Splitter parts = splitter.nextSplit('@');

            final Characteristics characteristics = characteristicsTransformer.unserialize(parts.nextPart());
            final int level = parts.nextPositiveInt();

            stats.put(level, characteristics);
        }

        return stats;
    }
}
