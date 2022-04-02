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

package fr.quatrevieux.araknemu.core.network.parser;

import fr.quatrevieux.araknemu.util.Splitter;
import org.checkerframework.checker.index.qual.GTENegativeOne;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.index.qual.Positive;

import java.util.NoSuchElementException;
import java.util.StringTokenizer;

/**
 * Decorator of {@link Splitter} for parse packet parts
 *
 * This class will only throw {@link ParsePacketException} if packet format is invalid instead of
 * {@link NoSuchElementException} or {@link IllegalArgumentException}
 *
 * @see StringTokenizer Inspired by this class
 * @see SinglePacketParser#tokenize(String, char) For create the instance
 */
public final class PacketTokenizer {
    private final String code;
    private final String input;
    private final Splitter splitter;

    /**
     * @param code The packet code {@link SinglePacketParser#code()}
     * @param input The packet arguments (i.e. packet without the code)
     * @param delimiter The delimiter
     */
    public PacketTokenizer(String code, String input, char delimiter) {
        this.code = code;
        this.input = input;
        this.splitter = new Splitter(input, delimiter);
    }

    /**
     * There is more parts on the string
     */
    public boolean hasNext() {
        return splitter.hasNext();
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
        return splitter.nextPartOrDefault(defaultValue);
    }

    /**
     * Get the next packet part
     * Unlike {@link StringTokenizer#nextToken()} empty parts are not skipped,
     * so an empty string can be returned by this method
     *
     * @throws ParsePacketException When no more parts is available (reach end of the packet)
     *
     * @see Splitter#nextPart()
     */
    public String nextPart() {
        try {
            return splitter.nextPart();
        } catch (NoSuchElementException e) {
            throw new ParsePacketException(code + input, "A part of the packet is missing");
        }
    }

    /**
     * Parse next part as integer
     *
     * @throws ParsePacketException When no more parts is available, or the number format is invalid
     */
    public int nextInt() {
        try {
            return splitter.nextInt();
        } catch (IllegalArgumentException e) {
            throw new ParsePacketException(code + input, "Invalid number", e);
        } catch (NoSuchElementException e) {
            throw new ParsePacketException(code + input, "A part of the packet is missing");
        }
    }

    /**
     * Parse next part as integer if exists, or get the default value
     * Unlike {@link Splitter#nextPartOrDefault(String)} the default value will be used
     * if the next part is an empty string
     *
     * @param defaultValue Default value to use if the next part is missing or empty
     *
     * @throws ParsePacketException When the number format is invalid
     */
    public int nextIntOrDefault(int defaultValue) {
        try {
            return splitter.nextIntOrDefault(defaultValue);
        } catch (IllegalArgumentException e) {
            throw new ParsePacketException(code + input, "Invalid number", e);
        }
    }

    /**
     * Parse next part as non negative or -1 (i.e. >= -1) integer
     *
     * @throws ParsePacketException When no more parts is available, or the number is invalid
     */
    public @GTENegativeOne int nextNonNegativeOrNegativeOneInt() {
        try {
            return splitter.nextNonNegativeOrNegativeOneInt();
        } catch (IllegalArgumentException e) {
            throw new ParsePacketException(code + input, "Invalid number", e);
        } catch (NoSuchElementException e) {
            throw new ParsePacketException(code + input, "A part of the packet is missing");
        }
    }

    /**
     * Parse next part as non negative or -1 (i.e. >= -1) integer if exists, or get the default value
     * Unlike {@link Splitter#nextPartOrDefault(String)} the default value will be used
     * if the next part is an empty string
     *
     * @param defaultValue Default value to use if the next part is missing or empty
     *
     * @throws ParsePacketException When the number is invalid
     */
    public @GTENegativeOne int nextNonNegativeOrNegativeOneIntOrDefault(@GTENegativeOne int defaultValue) {
        try {
            return splitter.nextNonNegativeOrNegativeOneIntOrDefault(defaultValue);
        } catch (IllegalArgumentException e) {
            throw new ParsePacketException(code + input, "Invalid number", e);
        }
    }

    /**
     * Parse next part as non negative (i.e. >= 0) integer
     *
     * @throws ParsePacketException When no more parts is available, or the number is invalid
     */
    public @NonNegative int nextNonNegativeInt() {
        try {
            return splitter.nextNonNegativeInt();
        } catch (IllegalArgumentException e) {
            throw new ParsePacketException(code + input, "Invalid number", e);
        } catch (NoSuchElementException e) {
            throw new ParsePacketException(code + input, "A part of the packet is missing");
        }
    }

    /**
     * Parse next part as non negative (i.e. >= 0) integer if exists, or get the default value
     * Unlike {@link Splitter#nextPartOrDefault(String)} the default value will be used
     * if the next part is an empty string
     *
     * @param defaultValue Default value to use if the next part is missing or empty
     *
     * @throws ParsePacketException When the number format is invalid, or is too low
     */
    public @NonNegative int nextNonNegativeIntOrDefault(@NonNegative int defaultValue) {
        try {
            return splitter.nextNonNegativeIntOrDefault(defaultValue);
        } catch (IllegalArgumentException e) {
            throw new ParsePacketException(code + input, "Invalid number", e);
        }
    }

    /**
     * Parse next part as positive (i.e. >= 1) integer
     *
     * @throws ParsePacketException When no more parts is available, or the number is invalid
     */
    public @Positive int nextPositiveInt() {
        try {
            return splitter.nextPositiveInt();
        } catch (IllegalArgumentException e) {
            throw new ParsePacketException(code + input, "Invalid number", e);
        } catch (NoSuchElementException e) {
            throw new ParsePacketException(code + input, "A part of the packet is missing");
        }
    }

    /**
     * Parse next part as positive (i.e. >= 1) integer if exists, or get the default value
     * Unlike {@link Splitter#nextPartOrDefault(String)} the default value will be used
     * if the next part is an empty string
     *
     * @param defaultValue Default value to use if the next part is missing or empty
     *
     * @throws ParsePacketException When the number format is invalid or is too low
     */
    public @Positive int nextPositiveIntOrDefault(@Positive int defaultValue) {
        try {
            return splitter.nextPositiveIntOrDefault(defaultValue);
        } catch (IllegalArgumentException e) {
            throw new ParsePacketException(code + input, "Invalid number", e);
        }
    }
}
