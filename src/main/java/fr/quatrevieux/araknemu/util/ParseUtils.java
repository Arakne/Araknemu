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
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.index.qual.Positive;
import org.checkerframework.common.value.qual.IntRange;

/**
 * Utility class for perform string parsing, with type check
 */
public final class ParseUtils {
    /**
     * Utility class: disable constructor
     */
    private ParseUtils() {
    }

    /**
     * Parse a positive integer (i.e. >= 1) string in base 10
     *
     * @throws NumberFormatException When the number format is invalid
     * @throws IllegalArgumentException When the number is too low
     */
    public static @Positive int parsePositiveInt(String value) {
        return Asserter.assertPositive(Integer.parseInt(value));
    }

    /**
     * Parse a non negative integer (i.e. >= 0) in base 10
     *
     * @throws NumberFormatException When the number format is invalid
     * @throws IllegalArgumentException When the number is too low
     */
    public static @NonNegative int parseNonNegativeInt(String value) {
        return Asserter.assertNonNegative(Integer.parseInt(value));
    }

    /**
     * Parse a non negative integer (i.e. >= 0)
     *
     * @param base The Number encoding base
     *
     * @throws NumberFormatException When the number format is invalid
     * @throws IllegalArgumentException When the number is too low
     */
    public static @NonNegative int parseNonNegativeInt(String value, @IntRange(from = Character.MIN_RADIX, to = Character.MAX_RADIX) int base) {
        return Asserter.assertNonNegative(Integer.parseInt(value, base));
    }

    /**
     * Parse a non negative integer (i.e. >= -1) in base 10
     *
     * @throws NumberFormatException When the number format is invalid
     * @throws IllegalArgumentException When the number is too low
     */
    public static @GTENegativeOne int parseNonNegativeOrNegativeOneInt(String value) {
        return Asserter.assertGTENegativeOne(Integer.parseInt(value));
    }

    /**
     * Parse a single decimal char to its int value
     *
     * <pre>{@code
     * ParseUtils.parseDecimalChar('0') == 0;
     * ParseUtils.parseDecimalChar('8') == 8;
     * }</pre>
     *
     * @throws IllegalArgumentException If the char is not in range '0' - '9'
     */
    public static @IntRange(from = 0, to = 9) int parseDecimalChar(char c) {
        final int parsed = c - '0';

        if (parsed > 9 || parsed < 0) {
            throw new IllegalArgumentException("The value must be in range 0-9");
        }

        return parsed;
    }

    /**
     * Parse the first character of the string decimal to its int value
     *
     * <pre>{@code
     * ParseUtils.parseDecimalChar("0") == 0;
     * ParseUtils.parseDecimalChar("8") == 8;
     * ParseUtils.parseDecimalChar("123") == 1; // Parse only the first char
     * }</pre>
     *
     * @throws IllegalArgumentException If the char is not in range '0' - '9', or if the string is empty
     */
    public static @IntRange(from = 0, to = 9) int parseDecimalChar(String str) {
        if (str.isEmpty()) {
            throw new IllegalArgumentException("The value must not be empty");
        }

        return parseDecimalChar(str.charAt(0));
    }
}
