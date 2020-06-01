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
 * Copyright (c) 2017-2020 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.core.network.util;

import fr.quatrevieux.araknemu.core.network.Channel;

import java.net.InetSocketAddress;
import java.util.Stack;

/**
 * Dummy implementation of channel
 */
final public class DummyChannel implements Channel {
    private long id = 1;
    private boolean isAlive = true;
    private Stack<Object> messages = new Stack<>();
    private DummyServer<?> server;

    @Override
    public Object id() {
        return id;
    }

    @Override
    public void write(Object message) {
        messages.push(message);
    }

    @Override
    public void close() {
        isAlive = false;

        if (server != null) {
            server.removeChannel(this);
        }
    }

    @Override
    public boolean isAlive() {
        return isAlive;
    }

    public Stack<Object> getMessages() {
        return messages;
    }

    public void setId(long id) {
        this.id = id;
    }

    void setServer(DummyServer<?> server) {
        this.server = server;
    }

    @Override
    public InetSocketAddress address() {
        return new InetSocketAddress("127.0.0.1", 0);
    }
}
