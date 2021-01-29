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
 * Constraint on value extracted from entity
 *
 * @param <T> The entity type
 * @param <E> The error type
 * @param <V> The value type
 */
abstract public class AbstractValueConstraint<T, E, V> implements EntityConstraint<T, E> {
    public interface Getter<T, V> {
        /**
         * Get the value from the entity
         */
        public V get(T entity);
    }

    final private E error;
    final private Getter<T, V> getter;

    public AbstractValueConstraint(E error, Getter<T, V> getter) {
        this.error = error;
        this.getter = getter;
    }

    @Override
    public boolean check(T entity) {
        return checkValue(getter.get(entity));
    }

    @Override
    public E error() {
        return error;
    }

    abstract protected boolean checkValue(V value);
}
