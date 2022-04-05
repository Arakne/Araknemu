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

package fr.quatrevieux.araknemu.data.world.transformer;

import fr.arakne.utils.encoding.Base64;
import fr.quatrevieux.araknemu.data.transformer.Transformer;
import fr.quatrevieux.araknemu.data.transformer.TransformerException;
import fr.quatrevieux.araknemu.util.Asserter;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.PolyNull;

import java.util.Arrays;

/**
 * Transform map fight places
 */
public final class FightPlacesTransformer implements Transformer<@NonNegative int[][]> {
    @Override
    public @PolyNull String serialize(@NonNegative int @PolyNull [][] value) {
        throw new UnsupportedOperationException();
    }

    @Override
    @SuppressWarnings("return") // returned values are non negative
    public @NonNegative int[][] unserialize(@PolyNull String serialize) {
        if (serialize == null) {
            return new int[0][];
        }

        try {
            return Arrays.stream(StringUtils.split(serialize, "|", 2))
                .map(this::parseTeamPlaces)
                .toArray(int[][]::new)
            ;
        } catch (RuntimeException e) {
            throw new TransformerException("Cannot parse places '" + serialize + "'", e);
        }
    }

    @SuppressWarnings("argument") // String indexes are safe
    private @NonNegative int[] parseTeamPlaces(String places) {
        final @NonNegative int[] cells = new int[places.length() / 2];

        for (int i = 0; i < cells.length; i++) {
            cells[i] = Asserter.assertNonNegative(Base64.decode(places.substring(2 * i, 2 * i + 2)));
        }

        return cells;
    }
}
