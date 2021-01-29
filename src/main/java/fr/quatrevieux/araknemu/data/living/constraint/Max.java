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

package fr.quatrevieux.araknemu.data.living.constraint;

/**
 * Check for maximum value
 *
 * @param <T> The entity type
 * @param <E> The error type
 * @param <V> The value type (should be comparable
 */
final public class Max<T, E, V extends Comparable> extends AbstractValueConstraint<T, E, V> {
    final private V value;

    public Max(E error, Getter<T, V> getter, V value) {
        super(error, getter);
        this.value = value;
    }

    @Override
    protected boolean checkValue(V value) {
        return value.compareTo(this.value) <= 0;
    }
}
