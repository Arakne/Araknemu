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
 * Delegates all session methods to an inner session
 */
public abstract class AbstractDelegatedSession implements Session {
    private final Session session;

    public AbstractDelegatedSession(Session session) {
        this.session = session;
    }

    @Override
    public final Channel channel() {
        return session.channel();
    }

    @Override
    public final void send(Object packet) {
        session.send(packet);
    }

    @Override
    public final void receive(Object packet) {
        session.receive(packet);
    }

    @Override
    public final void exception(Throwable cause) {
        session.exception(cause);
    }

    @Override
    public final void exception(Throwable cause, Object packet) {
        session.exception(cause, packet);
    }

    @Override
    public final void close() {
        session.close();
    }

    @Override
    public final boolean isAlive() {
        return session.isAlive();
    }
}
