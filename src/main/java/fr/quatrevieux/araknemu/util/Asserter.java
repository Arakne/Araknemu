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
 * Copyright (c) 2017-2022 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.util;

import org.checkerframework.checker.index.qual.GTENegativeOne;
import org.checkerframework.checker.index.qual.IndexFor;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.index.qual.Positive;
import org.checkerframework.common.value.qual.IntRange;

/**
 * Utility class for perform type assertions or casts
 */
public final class Asserter {
    /**
     * Utility class: disable constructor
     */
    private Asserter() {
    }

    /**
     * Ensure that the value is positive (i.e. >= 1)
     *
     * @throws IllegalArgumentException When the value is not positive
     */
    public static @Positive int assertPositive(int value) {
        if (value < 1) {
            throw new IllegalArgumentException("The value must be positive");
        }

        return value;
    }

    /**
     * Ensure that the value is non-negative (i.e. >= 0)
     *
     * @throws IllegalArgumentException When the value is negative
     */
    public static @NonNegative int assertNonNegative(int value) {
        if (value < 0) {
            throw new IllegalArgumentException("The value must be positive or null");
        }

        return value;
    }

    /**
     * Ensure that the value is non-negative (i.e. >= 0)
     *
     * @throws IllegalArgumentException When the value is negative
     */
    public static @NonNegative long assertNonNegative(long value) {
        if (value < 0) {
            throw new IllegalArgumentException("The value must be positive");
        }

        return value;
    }

    /**
     * Ensure that the value is greater than or equals 1
     *
     * @throws IllegalArgumentException When the value is too low
     */
    public static @GTENegativeOne int assertGTENegativeOne(int value) {
        if (value < -1) {
            throw new IllegalArgumentException("The value must be >= -1");
        }

        return value;
    }

    /**
     * Ensure that the value is a percent (i.e. in interval [0, 100])
     *
     * @throws IllegalArgumentException When the value is negative
     */
    public static @IntRange(from = 0, to = 100) int assertPercent(int value) {
        if (value < 0 || value > 100) {
            throw new IllegalArgumentException("The value must be in range [0, 100]");
        }

        return value;
    }

    /**
     * Ensure that the index parameter is a valid index for the given array
     *
     * @param array Array to check
     * @param index Index value to check
     *
     * @return Same value as "index" parameter
     *
     * @throws IndexOutOfBoundsException When the value is out of range
     */
    public static @IndexFor("#1") <T> int assertIndexFor(T[] array, int index) {
        if (index < 0 || index >= array.length) {
            throw new IndexOutOfBoundsException("Invalid index " + index + " for array of length " + array.length);
        }

        return index;
    }

    /**
     * Cast an int to a non-negative int without perform runtime check
     *
     * @see Asserter#assertNonNegative(int) For perform a runtime check of the value
     */
    @SuppressWarnings("return") // Checker do not resolve "assert" instruction
    public static @NonNegative int castNonNegative(int value) {
        assert value >= 0;

        return value;
    }

    /**
     * Cast an int to a non-negative int without perform runtime check
     *
     * @see Asserter#assertNonNegative(long) For perform a runtime check of the value
     */
    @SuppressWarnings("return") // Checker do not resolve "assert" instruction
    public static @NonNegative long castNonNegative(long value) {
        assert value >= 0;

        return value;
    }

    /**
     * Cast an int to a positive int without perform runtime check
     *
     * @see Asserter#assertPositive(int) For perform a runtime check of the value
     */
    @SuppressWarnings("return") // Checker do not resolve "assert" instruction
    public static @Positive int castPositive(int value) {
        assert value > 0;

        return value;
    }

    /**
     * Cast an int to a positive int without perform runtime check
     */
    @SuppressWarnings("return") // Checker do not resolve "assert" instruction
    public static @Positive long castPositive(long value) {
        assert value > 0;

        return value;
    }
}
