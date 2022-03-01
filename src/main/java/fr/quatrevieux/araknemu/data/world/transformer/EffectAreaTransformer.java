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
import fr.quatrevieux.araknemu.data.value.EffectArea;
import org.checkerframework.checker.nullness.qual.PolyNull;

/**
 * Transform spell / weapon effect area string
 */
public final class EffectAreaTransformer implements Transformer<EffectArea> {
    @Override
    public @PolyNull String serialize(@PolyNull EffectArea value) {
        if (value == null) {
            return null;
        }

        return new String(new char[] {value.type().c(), Base64.chr(value.size())});
    }

    @Override
    public @PolyNull EffectArea unserialize(@PolyNull String serialize) {
        if (serialize == null) {
            return null;
        }

        return new EffectArea(
            EffectArea.Type.byChar(serialize.charAt(0)),
            Base64.ord(serialize.charAt(1))
        );
    }
}
