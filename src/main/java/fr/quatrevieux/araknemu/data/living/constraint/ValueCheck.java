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
 * Check value using lambda
 *
 * @param <T> The entity type
 * @param <E> The error type
 * @param <V> The value type
 */
public final class ValueCheck<T, E, V> extends AbstractValueConstraint<T, E, V> {
    public interface Checker<V> {
        public boolean check(V value);
    }

    private final Checker<V> checker;

    public ValueCheck(E error, Getter<T, V> getter, Checker<V> checker) {
        super(error, getter);
        this.checker = checker;
    }

    @Override
    protected boolean checkValue(V value) {
        return checker.check(value);
    }
}
