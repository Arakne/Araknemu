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
import fr.quatrevieux.araknemu.core.network.Server;
import fr.quatrevieux.araknemu.core.network.SessionClosed;
import fr.quatrevieux.araknemu.core.network.session.Session;
import fr.quatrevieux.araknemu.core.network.session.SessionFactory;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Fake server implementation
 *
 * @param <S> The handled session type
 */
public final class DummyServer<S extends Session> implements Server<S> {
    private final SessionFactory<S> factory;
    private final Collection<S> sessions = new CopyOnWriteArrayList<>();
    private int lastId = 0;

    public DummyServer(SessionFactory<S> factory) {
        this.factory = factory;
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {
        sessions.clear();
    }

    @Override
    public Collection<S> sessions() {
        return sessions;
    }

    /**
     * Create a new session
     */
    public S createSession() {
        return createSession(null);
    }

    /**
     * Create a new session
     */
    public S createSession(String ipAddress) {
        final DummyChannel channel = new DummyChannel(ipAddress);

        channel.setId(++lastId);
        channel.setServer(this);

        final S session = factory.create(channel);

        sessions.add(session);

        return session;
    }

    void removeChannel(Channel channel) {
        sessions.removeIf(s -> {
            if (s.channel().equals(channel)) {
                s.receive(new SessionClosed());
                return true;
            }

            return false;
        });
    }
}
