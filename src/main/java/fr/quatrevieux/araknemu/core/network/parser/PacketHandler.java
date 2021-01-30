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
 * Handle a given packet type
 * @param <S> Session type
 * @param <P> Packet type to handle
 */
public interface PacketHandler<S, P extends Packet> {
    /**
     * Handle the incoming packet
     *
     * @throws Exception When an error occurs during handle the packet
     */
    public void handle(S session, P packet) throws Exception;

    /**
     * Get the handled packet class
     */
    public Class<P> packet();
}
