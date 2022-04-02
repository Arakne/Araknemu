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

import fr.quatrevieux.araknemu.data.constant.Effect;
import fr.quatrevieux.araknemu.data.transformer.Transformer;
import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;
import fr.quatrevieux.araknemu.util.Splitter;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.PolyNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Transform item effects
 */
public final class ItemEffectsTransformer implements Transformer<List<ItemTemplateEffectEntry>> {
    @Override
    public @NonNull String serialize(@PolyNull List<ItemTemplateEffectEntry> value) {
        if (value == null || value.isEmpty()) {
            return "";
        }

        final StringBuilder sb = new StringBuilder(value.size() * 8);

        for (ItemTemplateEffectEntry entry : value) {
            if (sb.length() > 0) {
                sb.append(',');
            }

            sb
                .append(Integer.toString(entry.effect().id(), 16)).append('#')
                .append(Integer.toString(entry.min(), 16)).append('#')
                .append(Integer.toString(entry.max(), 16)).append('#')
                .append(Integer.toString(entry.special(), 16)).append('#')
                .append(entry.text())
            ;
        }

        return sb.toString();
    }

    @Override
    public @NonNull List<ItemTemplateEffectEntry> unserialize(@PolyNull String serialize) {
        if (serialize == null || serialize.isEmpty()) {
            return new ArrayList<>();
        }

        final List<ItemTemplateEffectEntry> effects = new ArrayList<>();
        final Splitter splitter = new Splitter(serialize, ',');

        while (splitter.hasNext()) {
            final Splitter args = splitter.nextSplit('#');

            try {
                effects.add(
                    new ItemTemplateEffectEntry(
                        Effect.byId(args.nextInt(16)),
                        args.nextNonNegativeInt(16),
                        args.nextNonNegativeInt(16),
                        args.nextNonNegativeInt(16),
                        args.nextPartOrDefault("")
                    )
                );
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Cannot unserialize effect " + serialize, e);
            }
        }

        return effects;
    }
}
