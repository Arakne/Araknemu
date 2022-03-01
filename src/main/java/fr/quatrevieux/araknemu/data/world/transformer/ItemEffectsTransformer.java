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
import org.apache.commons.lang3.StringUtils;
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

        for (String part : StringUtils.split(serialize, ",")) {
            final String[] args = StringUtils.split(part, "#", 5);

            if (args.length < 4) {
                throw new IllegalArgumentException("Cannot unserialize effect " + serialize);
            }

            effects.add(
                new ItemTemplateEffectEntry(
                    Effect.byId(Integer.parseInt(args[0], 16)),
                    Integer.parseInt(args[1], 16),
                    Integer.parseInt(args[2], 16),
                    Integer.parseInt(args[3], 16),
                    args.length > 4 ? args[4] : ""
                )
            );
        }

        return effects;
    }
}
