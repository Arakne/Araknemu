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

import org.checkerframework.checker.builder.qual.CalledMethods;
import org.checkerframework.checker.nullness.qual.EnsuresNonNull;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.util.NullnessUtil;
import org.checkerframework.common.returnsreceiver.qual.This;

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
public class ConstraintBuilder<T, E extends @NonNull Object> {
    private final List<EntityConstraint<T, E>> constraints = new ArrayList<>();

    private AbstractValueConstraint.@MonotonicNonNull Getter<T, ?> getter;
    private @MonotonicNonNull E error;

    /**
     * Set the current value getter
     */
    @EnsuresNonNull({"this.getter"})
    public @This ConstraintBuilder<T, E> value(AbstractValueConstraint.Getter<T, ?> getter) {
        this.getter = getter;

        return this;
    }

    /**
     * Set the current error object
     */
    @EnsuresNonNull({"this.error"})
    public @This ConstraintBuilder<T, E> error(@NonNull E error) {
        this.error = error;

        return this;
    }

    /**
     * Ensure not empty
     */
    public @This ConstraintBuilder<T, E> notEmpty(@CalledMethods({"value", "error"}) ConstraintBuilder<T, E> this) {
        constraints.add(new NotEmpty<>(NullnessUtil.castNonNull(error), (AbstractValueConstraint.Getter<T, String>) NullnessUtil.castNonNull(getter)));

        return this;
    }

    /**
     * Check value by regex
     */
    public @This ConstraintBuilder<T, E> regex(@CalledMethods({"value", "error"}) ConstraintBuilder<T, E> this, String regex) {
        constraints.add(new Regex<>(NullnessUtil.castNonNull(error), (AbstractValueConstraint.Getter<T, String>) NullnessUtil.castNonNull(getter), regex));

        return this;
    }

    /**
     * Check with lambda expression
     */
    public @This ConstraintBuilder<T, E> check(@CalledMethods({"value", "error"}) ConstraintBuilder<T, E> this, ValueCheck.Checker checker) {
        constraints.add(new ValueCheck<>(NullnessUtil.castNonNull(error), NullnessUtil.castNonNull(getter), checker));

        return this;
    }

    /**
     * Maximum allowed value
     */
    public @This <V extends Comparable> ConstraintBuilder<T, E> max(@CalledMethods({"value", "error"}) ConstraintBuilder<T, E> this, V value) {
        constraints.add(new Max<>(NullnessUtil.castNonNull(error), (AbstractValueConstraint.Getter<T, V>) NullnessUtil.castNonNull(getter), value));

        return this;
    }

    /**
     * Minimum string length
     */
    public @This ConstraintBuilder<T, E> minLength(@CalledMethods({"value", "error"}) ConstraintBuilder<T, E> this, int length) {
        constraints.add(new MinLength<>(NullnessUtil.castNonNull(error), (AbstractValueConstraint.Getter<T, String>) NullnessUtil.castNonNull(getter), length));

        return this;
    }

    /**
     * Maximum string length
     */
    public @This ConstraintBuilder<T, E> maxLength(@CalledMethods({"value", "error"}) ConstraintBuilder<T, E> this, int length) {
        constraints.add(new MaxLength<>(NullnessUtil.castNonNull(error), (AbstractValueConstraint.Getter<T, String>) NullnessUtil.castNonNull(getter), length));

        return this;
    }

    /**
     * Lambda check for the entire entity value
     */
    public @This ConstraintBuilder<T, E> entityCheck(@CalledMethods({"value", "error"}) ConstraintBuilder<T, E> this, EntityCheck.Checker<T> checker) {
        constraints.add(new EntityCheck<>(NullnessUtil.castNonNull(error), checker));

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
    @SuppressWarnings("monotonic")
    public @This ConstraintBuilder<T, E> not(BuilderFactory<T, E> factory) {
        final ConstraintBuilder<T, E> builder = new ConstraintBuilder<>();

        builder.error  = error;
        builder.getter = getter;

        factory.build(builder);

        constraints.add(new Not<>(builder.build()));

        return this;
    }

    /**
     * Build the constraint
     */
    public EntityConstraint<T, @NonNull E> build() {
        if (constraints.size() == 1) {
            return constraints.get(0);
        }

        return new Must<>(
            constraints.toArray(new EntityConstraint[0])
        );
    }
}
