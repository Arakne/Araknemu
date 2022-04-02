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
 * Copyright (c) 2017-2021 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.player.characteristic;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.Characteristics;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Objects;

/**
 * Decorator for add computed characteristics over base characteristics object
 *
 * Computes {@link Characteristic#RESISTANCE_ACTION_POINT} and {@link Characteristic#RESISTANCE_MOVEMENT_POINT}
 * using inner {@link Characteristic#WISDOM} value
 *
 * @see MutableComputedCharacteristics To decorate {@link fr.quatrevieux.araknemu.game.world.creature.characteristics.MutableCharacteristics}
 *
 * @param <C> The inner characteristics object type
 */
public class ComputedCharacteristics<C extends Characteristics> implements Characteristics {
    public static final int POINT_RESISTANCE_FACTOR = 4;

    protected final C inner;

    public ComputedCharacteristics(C inner) {
        this.inner = inner;
    }

    @Override
    public final int get(Characteristic characteristic) {
        final int baseValue = inner.get(characteristic);

        if (
            characteristic != Characteristic.RESISTANCE_MOVEMENT_POINT
            && characteristic != Characteristic.RESISTANCE_ACTION_POINT
        ) {
            return baseValue;
        }

        return baseValue + inner.get(Characteristic.WISDOM) / POINT_RESISTANCE_FACTOR;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof ComputedCharacteristics)) {
            return false;
        }

        final ComputedCharacteristics<?> that = (ComputedCharacteristics<?>) o;

        return Objects.equals(inner, that.inner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(inner);
    }
}
