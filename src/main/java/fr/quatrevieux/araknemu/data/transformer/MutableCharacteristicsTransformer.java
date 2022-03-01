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

package fr.quatrevieux.araknemu.data.transformer;

import fr.quatrevieux.araknemu.game.world.creature.characteristics.DefaultCharacteristics;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.MutableCharacteristics;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.PolyNull;

/**
 * Adapter for transform mutable characteristics
 */
public final class MutableCharacteristicsTransformer implements Transformer<MutableCharacteristics> {
    private final CharacteristicsTransformer inner = new CharacteristicsTransformer();

    @Override
    public @PolyNull String serialize(@PolyNull MutableCharacteristics value) {
        return inner.serialize(value);
    }

    @Override
    public @NonNull MutableCharacteristics unserialize(@PolyNull String serialize) {
        final MutableCharacteristics characteristics = inner.unserialize(serialize);

        if (characteristics == null) {
            return new DefaultCharacteristics();
        }

        return characteristics;
    }
}
