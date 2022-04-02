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
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.PolyNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Transform map fight places
 *
 * @todo use int[][] for fight places
 */
public final class FightPlacesTransformer implements Transformer<List<Integer>[]> {
    @Override
    public @PolyNull String serialize(List @PolyNull[] value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List @NonNull[] unserialize(@PolyNull String serialize) {
        if (serialize == null) {
            return new List[0];
        }

        try {
            return Arrays.stream(StringUtils.split(serialize, "|", 2))
                .map(this::parseTeamPlaces)
                .toArray(List[]::new)
            ;
        } catch (RuntimeException e) {
            throw new TransformerException("Cannot parse places '" + serialize + "'", e);
        }
    }

    private List<Integer> parseTeamPlaces(String places) {
        final List<Integer> cells = new ArrayList<>(places.length() / 2);

        for (int i = 0; i < places.length() - 1; i += 2) {
            cells.add(Base64.decode(places.substring(i, i + 2)));
        }

        return cells;
    }
}
