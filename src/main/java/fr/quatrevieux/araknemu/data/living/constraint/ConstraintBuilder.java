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

import java.util.ArrayList;
import java.util.List;

/**
 * Builder for entity constraint
 *
 * ConstraintBuilder builder = new ConstraintBuilder();
 *
 * builder
 *     .error(Error.forValue)
 *     .value(entity::getter)
 *     .notEmpty()
 *     .regex("\\w{4,12}")
 *
 *     .error(Error.other)
 *     .entityCheck(repository::check)
 * ;
 *
 * builder.build();
 *
 * @param <T> The entity type
 * @param <E> The error type
 */
public class ConstraintBuilder<T, E> {
    final private List<EntityConstraint<T, E>> constraints = new ArrayList<>();

    private AbstractValueConstraint.Getter<T, ?> getter;
    private E error;

    /**
     * Set the current value getter
     */
    public ConstraintBuilder<T, E> value(AbstractValueConstraint.Getter<T, ?> getter) {
        this.getter = getter;

        return this;
    }

    /**
     * Set the current error object
     */
    public ConstraintBuilder<T, E> error(E error) {
        this.error = error;

        return this;
    }

    /**
     * Ensure not empty
     */
    public ConstraintBuilder<T, E> notEmpty() {
        constraints.add(new NotEmpty<>(error, (AbstractValueConstraint.Getter<T, String>) getter));

        return this;
    }

    /**
     * Check value by regex
     */
    public ConstraintBuilder<T, E> regex(String regex) {
        constraints.add(new Regex<>(error, (AbstractValueConstraint.Getter<T, String>) getter, regex));

        return this;
    }

    /**
     * Check with lambda expression
     */
    public ConstraintBuilder<T, E> check(ValueCheck.Checker checker) {
        constraints.add(new ValueCheck<>(error, getter, checker));

        return this;
    }

    /**
     * Maximum allowed value
     */
    public <V extends Comparable> ConstraintBuilder<T, E> max(V value) {
        constraints.add(new Max<>(error, (AbstractValueConstraint.Getter<T, V>) getter, value));

        return this;
    }

    /**
     * Minimum string length
     */
    public ConstraintBuilder<T, E> minLength(int length) {
        constraints.add(new MinLength<>(error, (AbstractValueConstraint.Getter<T, String>) getter, length));

        return this;
    }

    /**
     * Maximum string length
     */
    public ConstraintBuilder<T, E> maxLength(int length) {
        constraints.add(new MaxLength<>(error, (AbstractValueConstraint.Getter<T, String>) getter, length));

        return this;
    }

    /**
     * Lambda check for the entire entity value
     */
    public ConstraintBuilder<T, E> entityCheck(EntityCheck.Checker<T> checker) {
        constraints.add(new EntityCheck<>(error, checker));

        return this;
    }

    /**
     * Reverse the check value
     *
     * builder.not(b ->
     *     b.check(...)
     *      .check(...)
     * );
     */
    public ConstraintBuilder<T, E> not(BuilderFactory<T, E> factory) {
        ConstraintBuilder<T, E> builder = new ConstraintBuilder<>();

        builder.error  = error;
        builder.getter = getter;

        factory.build(builder);

        constraints.add(new Not<>(builder.build()));

        return this;
    }

    /**
     * Build the constraint
     */
    public EntityConstraint<T, E> build() {
        if (constraints.size() == 1) {
            return constraints.get(0);
        }

        return new Must<>(
            constraints.toArray(new EntityConstraint[] {})
        );
    }
}
