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

import fr.quatrevieux.araknemu.game.world.creature.characteristics.Characteristics;
import org.checkerframework.checker.nullness.qual.PolyNull;

/**
 * Adapter for transform immutable characteristics
 */
public final class ImmutableCharacteristicsTransformer implements Transformer<Characteristics> {
    private final CharacteristicsTransformer inner = new CharacteristicsTransformer();

    @Override
    public @PolyNull String serialize(@PolyNull Characteristics value) {
        return inner.serialize(value);
    }

    @Override
    public @PolyNull Characteristics unserialize(@PolyNull String serialize) {
        return inner.unserialize(serialize);
    }
}
