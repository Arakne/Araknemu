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
import fr.quatrevieux.araknemu.data.transformer.TransformerException;
import fr.quatrevieux.araknemu.util.Splitter;
import org.checkerframework.checker.index.qual.Positive;
import org.checkerframework.checker.nullness.qual.PolyNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Transformer for parse exchange items
 *
 * @see fr.quatrevieux.araknemu.data.world.entity.environment.npc.NpcExchange#requiredItems()
 */
public final class ExchangeItemsTransformer implements Transformer<Map<Integer, @Positive Integer>> {
    @Override
    public @PolyNull String serialize(@PolyNull Map<Integer, @Positive Integer> value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public @PolyNull Map<Integer, @Positive Integer> unserialize(@PolyNull String serialize) throws TransformerException {
        if (serialize == null) {
            return null;
        }

        if (serialize.isEmpty()) {
            return new HashMap<>();
        }

        final Map<Integer, @Positive Integer> items = new HashMap<>();
        final Splitter splitter = new Splitter(serialize, ';');

        while (splitter.hasNext()) {
            final Splitter parts = splitter.nextSplit(':');

            items.put(
                parts.nextInt(),
                parts.nextPositiveIntOrDefault(1)
            );
        }

        return items;
    }
}
