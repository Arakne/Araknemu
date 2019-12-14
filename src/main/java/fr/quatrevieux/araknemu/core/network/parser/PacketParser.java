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

/**
 * Parse an incoming packet
 */
public interface PacketParser {
    /**
     * Parse an get packet from the incoming data
     *
     * @param input Incoming raw data
     * @return The packet object
     *
     * @throws UndefinedPacketException When cannot identify the packet
     * @throws ParsePacketException When cannot parse the packet
     */
    public Packet parse(String input) throws ParsePacketException;
}
