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
 * Check for entire entity value
 *
 * @param <T> Entity type
 * @param <E> Error type
 */
public final class EntityCheck<T, E> implements EntityConstraint<T, E> {
    private final E error;
    private final Checker<T> checker;

    public EntityCheck(E error, Checker<T> checker) {
        this.error = error;
        this.checker = checker;
    }

    @Override
    public boolean check(T entity) {
        return checker.check(entity);
    }

    @Override
    public E error() {
        return error;
    }

    public interface Checker<T> {
        public boolean check(T entity);
    }
}
