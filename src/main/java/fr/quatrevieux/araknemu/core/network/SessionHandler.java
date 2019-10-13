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

package fr.quatrevieux.araknemu.core.network;

/**
 * Base handler for session
 */
public interface SessionHandler<S extends Session> {
    /**
     * The session is opened
     */
    public void opened(S session) throws Exception;

    /**
     * The session is closed
     */
    public void closed(S session) throws Exception;

    /**
     * A message is received from the session
     *
     * @param session The session
     * @param packet The received packet
     */
    public void received(S session, String packet) throws Exception;

    /**
     * A packet is sent to the session
     *
     * @param session The receiver
     * @param packet The sent packet
     */
    public void sent(S session, Object packet) throws Exception;

    /**
     * An error occurs during handling the session
     *
     * @param session The session in error
     * @param cause The cause
     */
    public void exception(S session, Throwable cause);

    /**
     * Create the session from the channel
     */
    public S create(Channel channel);
}
