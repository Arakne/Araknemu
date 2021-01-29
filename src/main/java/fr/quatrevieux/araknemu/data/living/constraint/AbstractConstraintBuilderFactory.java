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
 * Base entity constraint class for build constraints
 *
 * @param <T> The entity type
 * @param <E> The error type
 */
abstract public class AbstractConstraintBuilderFactory<T, E> implements EntityConstraint<T, E>, BuilderFactory<T, E> {
    private E error;
    private EntityConstraint<T, E> constraint;

    @Override
    public boolean check(T entity) {
        return constraint().check(entity);
    }

    @Override
    public E error() {
        return constraint().error();
    }

    private EntityConstraint<T, E> constraint() {
        if (constraint != null) {
            return constraint;
        }

        ConstraintBuilder<T, E> builder = new ConstraintBuilder<>();

        build(builder);

        return constraint = builder.build();
    }
}
