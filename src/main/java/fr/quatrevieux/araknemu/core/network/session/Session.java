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

package fr.quatrevieux.araknemu.core.network.session;

import fr.quatrevieux.araknemu.core.network.Channel;

/**
 * Base interface for sessions
 */
public interface Session {
    /**
     * Get the low level IO channel
     */
    public Channel channel();

    /**
     * Write packet to channel
     *
     * @param packet Packet to send
     */
    public void send(Object packet);

    /**
     * Handle a received packet
     *
     * @param packet Packet to handle
     */
    public void receive(Object packet);

    /**
     * Handle an exception raised on the current session
     *
     * @param cause Exception to handle
     */
    public void exception(Throwable cause);

    /**
     * Handle an exception raised on the current session, with the packet that caused the exception
     *
     * @param cause Exception to handle
     * @param packet Packet that caused the exception
     */
    public default void exception(Throwable cause, Object packet) {
        exception(cause);
    }

    /**
     * Close the session
     */
    public void close();

    /**
     * Check if the session is valid
     */
    public boolean isAlive();
}
