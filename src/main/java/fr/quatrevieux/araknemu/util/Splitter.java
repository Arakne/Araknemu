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

import java.util.NoSuchElementException;
import java.util.StringTokenizer;

/**
 * Split string parts separated by a character, and provide method for parsing parts to java primitives
 * This class can be overridden for provide extra parsing methods
 *
 * Unlike {@link StringTokenizer} :
 * - empty string are not skipped
 * - separator can only be a single character
 *
 * Usage:
 * <pre>{@code
 * // For "fixed" string format :
 * final Splitter splitter = new Splitter("foo,bar;15;fa,fc;;0", ';'); // Split string using ';'
 *
 * final Splitter begin = splitter.nextSplit(','); // Sub-split the first part
 *
 * begin.nextPart(); // "foo"
 * begin.nextPart(); // "bar"
 *
 * splitter.nextInt(); // 15
 *
 * final Splitter range = splitter.nextSplit(',');
 *
 * range.nextNonNegativeInt(16); // 250
 * range.nextNonNegativeInt(16); // 252
 *
 * splitter.nextPart(); // "" : do not skip empty strings
 * splitter.nextPart(); // "0"
 * splitter.nextPartOrDefault("###"); // "###" : use default value because there is not more parts
 *
 * // For "iterator style" parsing of dynamic string format :
 *
 * final Splitter splitter = new Splitter(myString, ';');
 *
 * // Will the end is not reached
 * while (splitter.hasNext()) {
 *     final Splitter parts = splitter.nextSplit(','); // Process the current part
 *
 *     parts.nextInt();
 *     parts.nextPart();
 *     // ...
 * }
 * }</pre>
 *
 * @see StringTokenizer Inspired by this class
 */
public final class Splitter {
    private final String string;
    private final char delimiter;

    private @NonNegative int currentPosition = 0;

    /**
     * @param string String to split
     * @param delimiter The delimiter
     */
    public Splitter(String string, char delimiter) {
        this.string = string;
        this.delimiter = delimiter;
    }

    /**
     * Get the inner string value
     */
    public String value() {
        return string;
    }

    /**
     * Get the next packet part
     * Unlike {@link StringTokenizer#nextToken()} empty parts are not skipped,
     * so an empty string can be returned by this method
     *
     * @throws NoSuchElementException When no more parts is available (reach end of the string)
     */
    public String nextPart() {
        final int currentPosition = this.currentPosition;
        final String packet = this.string;

        if (currentPosition > packet.length()) {
            throw new NoSuchElementException();
        }

        int nextPosition = packet.indexOf(delimiter, currentPosition);

        if (nextPosition == -1) {
            nextPosition = packet.length();
        }

        final String token = packet.substring(currentPosition, nextPosition);
        this.currentPosition = nextPosition + 1;

        return token;
    }

    /**
     * There is more parts on the string
     */
    public boolean hasNext() {
        return currentPosition <= string.length();
    }

    /**
     * Get the next part if exists, or return the default value
     *
     * The default value is only used if the part is missing, not if the part is an empty string.
     * So the code : {@code new Splitter("").nextPartOrDefault("foo")} will return "", and not "foo"
     *
     * @param defaultValue The used value if the next part is missing
     */
    public String nextPartOrDefault(String defaultValue) {
        if (!hasNext()) {
            return defaultValue;
        }

        return nextPart();
    }

    /**
     * Parse next part as integer
     *
     * @throws NoSuchElementException When no more parts is available (reach end of the packet)
     * @throws NumberFormatException When the number format is invalid
     */
    public int nextInt() {
        return Integer.parseInt(nextPart());
    }

    /**
     * Parse next part as integer
     *
     * @param base Number encoding base
     *
     * @throws NoSuchElementException When no more parts is available (reach end of the packet)
     * @throws NumberFormatException When the number format is invalid
     */
    public int nextInt(@IntRange(from = Character.MIN_RADIX, to = Character.MAX_RADIX) int base) {
        return Integer.parseInt(nextPart(), base);
    }

    /**
     * Parse next part as integer if exists, or get the default value
     * Unlike {@link Splitter#nextPartOrDefault(String)} the default value will be used
     * if the next part is an empty string
     *
     * @param defaultValue Default value to use if the next part is missing or empty
     *
     * @throws NumberFormatException When the number format is invalid
     */
    public int nextIntOrDefault(int defaultValue) {
        if (!hasNext()) {
            return defaultValue;
        }

        final String part = nextPart();

        return part.isEmpty() ? defaultValue : Integer.parseInt(part);
    }

    /**
     * Parse next part as non negative or -1 (i.e. >= -1) integer
     *
     * @throws NoSuchElementException When no more parts is available (reach end of the packet)
     * @throws NumberFormatException When the number format is invalid
     * @throws IllegalArgumentException When the number is too low
     */
    public @GTENegativeOne int nextNonNegativeOrNegativeOneInt() {
        return ParseUtils.parseNonNegativeOrNegativeOneInt(nextPart());
    }

    /**
     * Parse next part as non negative or -1 (i.e. >= -1) integer if exists, or get the default value
     * Unlike {@link Splitter#nextPartOrDefault(String)} the default value will be used
     * if the next part is an empty string
     *
     * @param defaultValue Default value to use if the next part is missing or empty
     *
     * @throws NumberFormatException When the number format is invalid
     * @throws IllegalArgumentException When the number is too low
     */
    public @GTENegativeOne int nextNonNegativeOrNegativeOneIntOrDefault(@GTENegativeOne int defaultValue) {
        if (!hasNext()) {
            return defaultValue;
        }

        final String part = nextPart();

        return part.isEmpty() ? defaultValue : ParseUtils.parseNonNegativeOrNegativeOneInt(part);
    }

    /**
     * Parse next part as non negative (i.e. >= 0) integer
     *
     * @param base Number encoding base
     *
     * @throws NoSuchElementException When no more parts is available (reach end of the packet)
     * @throws NumberFormatException When the number format is invalid
     * @throws IllegalArgumentException When the number is too low
     */
    public @NonNegative int nextNonNegativeInt(@IntRange(from = Character.MIN_RADIX, to = Character.MAX_RADIX) int base) {
        return ParseUtils.parseNonNegativeInt(nextPart(), base);
    }

    /**
     * Parse next part as non negative (i.e. >= 0) integer
     *
     * @throws NoSuchElementException When no more parts is available (reach end of the packet)
     * @throws NumberFormatException When the number format is invalid
     * @throws IllegalArgumentException When the number is too low
     */
    public @NonNegative int nextNonNegativeInt() {
        return ParseUtils.parseNonNegativeInt(nextPart());
    }

    /**
     * Parse next part as non negative (i.e. >= 0) integer if exists, or get the default value
     * Unlike {@link Splitter#nextPartOrDefault(String)} the default value will be used
     * if the next part is an empty string
     *
     * @param defaultValue Default value to use if the next part is missing or empty
     *
     * @throws NumberFormatException When the number format is invalid
     * @throws IllegalArgumentException When the number is too low
     */
    public @NonNegative int nextNonNegativeIntOrDefault(@NonNegative int defaultValue) {
        if (!hasNext()) {
            return defaultValue;
        }

        final String part = nextPart();

        return part.isEmpty() ? defaultValue : ParseUtils.parseNonNegativeInt(part);
    }

    /**
     * Parse next part as positive (i.e. >= 1) integer
     *
     * @throws NoSuchElementException When no more parts is available (reach end of the packet)
     * @throws NumberFormatException When the number format is invalid
     * @throws IllegalArgumentException When the number is too low
     */
    public @Positive int nextPositiveInt() {
        return ParseUtils.parsePositiveInt(nextPart());
    }

    /**
     * Parse next part as positive (i.e. >= 1) integer if exists, or get the default value
     * Unlike {@link Splitter#nextPartOrDefault(String)} the default value will be used
     * if the next part is an empty string
     *
     * @param defaultValue Default value to use if the next part is missing or empty
     *
     * @throws NumberFormatException When the number format is invalid
     * @throws IllegalArgumentException When the number is too low
     */
    public @Positive int nextPositiveIntOrDefault(@Positive int defaultValue) {
        if (!hasNext()) {
            return defaultValue;
        }

        final String part = nextPart();

        return part.isEmpty() ? defaultValue : ParseUtils.parsePositiveInt(part);
    }

    /**
     * Split the next token using given delimiter
     *
     * This method is equivalent to `new Splitter(splitter.nextPart(), delimiter);`
     */
    public Splitter nextSplit(char delimiter) {
        return new Splitter(nextPart(), delimiter);
    }
}
