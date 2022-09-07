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

import org.checkerframework.common.value.qual.MinLen;

/**
 * Parser for one packet
 */
public interface SinglePacketParser<P extends Packet> extends PacketParser {
    @Override
    public P parse(String input) throws ParsePacketException;

    /**
     * Get the packet identification code
     * This code is the 2 or 3 first chars of the incoming packet
     */
    public @MinLen(2) String code();

    /**
     * Create the tokenizer for the current packet
     *
     * @param input The packet data to parse
     * @param delimiter The parts delimiter
     *
     * @return The tokenizer instance
     */
    public default PacketTokenizer tokenize(String input, char delimiter) {
        return new PacketTokenizer(code(), input, delimiter);
    }
}
