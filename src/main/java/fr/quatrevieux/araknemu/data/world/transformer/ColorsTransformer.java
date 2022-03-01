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

import fr.arakne.utils.value.Colors;
import fr.quatrevieux.araknemu.data.transformer.Transformer;
import fr.quatrevieux.araknemu.data.transformer.TransformerException;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.PolyNull;

/**
 * Transform {@link Colors} database data
 *
 * Colors are 3 hexa numbers, separated by a comma ","
 * Default color is "-1"
 *
 * Ex: ffebcd,-1,ffebcd
 */
public final class ColorsTransformer implements Transformer<Colors> {
    @Override
    public @PolyNull String serialize(@PolyNull Colors value) {
        return value == null ? null : value.toHexString(",");
    }

    @Override
    public @PolyNull Colors unserialize(@PolyNull String serialize) throws TransformerException {
        if (serialize == null) {
            return null;
        }

        if ("-1,-1,-1".equals(serialize)) {
            return Colors.DEFAULT;
        }

        final String[] parts = StringUtils.split(serialize, ",", 3);

        if (parts.length != 3) {
            throw new TransformerException("Invalid color expression '" + serialize + "'");
        }

        return new Colors(
            parts[0].equals("-1") ? -1 : Integer.parseInt(parts[0], 16),
            parts[1].equals("-1") ? -1 : Integer.parseInt(parts[1], 16),
            parts[2].equals("-1") ? -1 : Integer.parseInt(parts[2], 16)
        );
    }
}
