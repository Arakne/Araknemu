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
 * Copyright (c) 2017-2024 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.data.transformer;

import fr.quatrevieux.araknemu.data.value.SpellTarget;
import org.checkerframework.checker.nullness.qual.PolyNull;

/**
 * Transformer for spell effect target flags
 *
 * Parse format: "normal,critical;normal;..."
 * Example: "1,2;3;4,5"
 *
 * When critical is not specified, it's the same as normal
 */
public final class SpellTargetsTransformer implements Transformer<SpellTarget[]> {
    @Override
    public @PolyNull String serialize(SpellTarget @PolyNull [] value) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public SpellTarget @PolyNull [] unserialize(@PolyNull String serialize) throws TransformerException {
        if (serialize == null) {
            return null;
        }

        final String[] parts = serialize.split(";");

        if (parts.length == 1 && parts[0].isEmpty()) {
            return new SpellTarget[0];
        }

        final SpellTarget[] targets = new SpellTarget[parts.length];

        for (int i = 0; i < parts.length; i++) {
            final String[] subParts = parts[i].split(",", 2);

            final int normal = Integer.parseInt(subParts[0]);
            final int critical;

            if (subParts.length > 1) {
                critical = Integer.parseInt(subParts[1]);
            } else {
                critical = normal;
            }

            targets[i] = new SpellTarget(normal, critical);
        }

        return targets;
    }
}
