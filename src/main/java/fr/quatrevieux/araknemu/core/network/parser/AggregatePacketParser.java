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
 * Copyright (c) 2017-2024 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.core.network.parser;

import org.checkerframework.checker.index.qual.LTEqLengthOf;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.common.value.qual.ArrayLen;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * Base packet parser
 * Will aggregate multiple {@link SinglePacketParser} for parse the incoming packet
 *
 * This implementation use a dictionary tree to store the parsers,
 * where each letter of the packet code is converted to an index in the internal array of sub-parsers,
 * and the used parser is the deepest one in the tree.
 *
 * For example, with the packet GA500;123 (GA is the packet code):
 * - Get the first letter of the packet code (G)
 * - G is converted to an index (6) and the sub-parser at this index is used
 * - A sub-parser is found, so the next letter (A) is used to find the next sub-parser
 * - A is converted to an index (0) and the sub-parser at this index is used
 * - The next letter (5) is out of the charset, so the previous parser is called
 *
 * So, this is equivalent of calling :
 * {@code parser.parsers['G' - A']['A' - 'A'].parse("GA500;123".substring(2))}
 */
public final class AggregatePacketParser implements PacketParser {
    /**
     * The lowest (i.e. lower ascii code) character allowed in the packet code
     */
    private static final char MIN_CHAR = 'A';

    /**
     * The highest (i.e. higher ascii code) character allowed in the packet code
     */
    private static final char MAX_CHAR = 'z';

    /**
     * The size of the allowed charset of packet code
     * Allow only uppercase and lowercase letters
     */
    private static final int CHARSET_SIZE = MAX_CHAR - MIN_CHAR + 1;

    /**
     * The depth of this parser in the dictionary tree
     * A depth of 0 means that this parser is the root of the tree
     */
    private final @NonNegative int depth;

    /**
     * Lookup table for the sub-parsers, where each index correspond to a letter of the packet code, at the current depth
     *
     * If the sub-parser is null, it means that there is no parser for the corresponding letter,
     * so the current parser should be used
     *
     * If the sub-parser is not null, the next letter of the packet code should be used to find the next parser
     *
     * The index is computed using the formula: {@code index = input.charAt(depth) - MIN_CHAR}
     */
    private final @Nullable AggregatePacketParser @ArrayLen(CHARSET_SIZE) [] parsers = new AggregatePacketParser[CHARSET_SIZE];

    /**
     * The actual parser for the current packet code.
     * If this value is null, {@see UndefinedPacketException} will be thrown
     */
    private @Nullable PacketParser parser = null;

    public AggregatePacketParser(SinglePacketParser<?>[] parsers) {
        this(0, Arrays.asList(parsers));
    }

    public AggregatePacketParser(Collection<SinglePacketParser<?>> parsers) {
        this(0, parsers);
    }

    private AggregatePacketParser(@NonNegative int depth, Collection<SinglePacketParser<?>> parsers) {
        this.depth = depth;

        for (SinglePacketParser<?> parser : parsers) {
            this.register(parser);
        }
    }

    @Override
    public Packet parse(String input) throws ParsePacketException {
        AggregatePacketParser current = this;
        final int len = input.length();
        @LTEqLengthOf("input") int codeLen;

        for (codeLen = 0; codeLen < len; ++codeLen) {
            final char c = input.charAt(codeLen);
            final int index = c - MIN_CHAR;

            if (index < 0 || index >= CHARSET_SIZE) {
                break;
            }

            final AggregatePacketParser subParser = current.parsers[index];

            if (subParser == null) {
                break;
            }

            current = subParser;
        }

        final PacketParser parser = current.parser;

        if (parser == null) {
            throw new UndefinedPacketException(input);
        }

        return parser.parse(input.substring(codeLen));
    }

    /**
     * Register a new parse
     * If a parse with the same code is already registered, it will be overridden
     *
     * @param parser New parser
     */
    public void register(SinglePacketParser<?> parser) {
        final String code = parser.code();
        final int len = code.length();
        final int depth = this.depth;

        assert depth <= len; // Should never happen, because parsers with depth > 0 are only called internally

        if (depth == len) {
            this.parser = parser;
            return;
        }

        final @SuppressWarnings("argument") char c = code.charAt(depth); // checkerframework is unable to infer that depth < len
        final int index = c - MIN_CHAR;

        if (index < 0 || index >= CHARSET_SIZE) {
            throw new IllegalArgumentException("Invalid character in packet code: " + code);
        }

        final @Nullable AggregatePacketParser subParser = parsers[index];

        if (subParser != null) {
            subParser.register(parser);
        } else {
            parsers[index] = new AggregatePacketParser(depth + 1, Collections.singleton(parser));
        }
    }
}
