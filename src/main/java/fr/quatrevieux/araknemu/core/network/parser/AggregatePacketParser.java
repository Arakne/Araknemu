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

package fr.quatrevieux.araknemu.core.network.parser;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Base packet parser
 * Will aggregate multiple {@link SinglePacketParser} for parse the incoming packet
 */
final public class AggregatePacketParser implements PacketParser {
    final private ConcurrentMap<String, SinglePacketParser> parsers = new ConcurrentHashMap<>();

    private int minCodeLength = 2;
    private int maxCodeLength = 2;

    public AggregatePacketParser(SinglePacketParser[] parsers) {
        for (SinglePacketParser parser : parsers) {
            this.register(parser);
        }
    }

    public AggregatePacketParser(Collection<SinglePacketParser> parsers) {
        for (SinglePacketParser parser : parsers) {
            this.register(parser);
        }
    }

    @Override
    public Packet parse(String input) throws ParsePacketException {
        for (int len = Math.min(maxCodeLength, input.length()); len >= minCodeLength; --len) {
            final String header = input.substring(0, len);

            if (parsers.containsKey(header)) {
                return parsers
                    .get(header)
                    .parse(input.substring(len))
                ;
            }
        }

        throw new UndefinedPacketException(input);
    }

    /**
     * Register a new parse
     * If a parse with the same code is already registered, it will be override
     * @param parser New parser
     */
    public void register(SinglePacketParser parser) {
        parsers.put(parser.code(), parser);

        if (parser.code().length() < minCodeLength) {
            minCodeLength = parser.code().length();
        }

        if (parser.code().length() > maxCodeLength) {
            maxCodeLength = parser.code().length();
        }
    }
}
