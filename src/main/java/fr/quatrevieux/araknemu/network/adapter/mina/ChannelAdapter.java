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

package fr.quatrevieux.araknemu.network.adapter.mina;

import fr.quatrevieux.araknemu.network.adapter.Channel;
import org.apache.mina.core.session.IoSession;

/**
 * Adapt apache mine IoSession to Channel
 */
final public class ChannelAdapter implements Channel {
    final private IoSession session;

    public ChannelAdapter(IoSession session) {
        this.session = session;
    }

    @Override
    public Object id() {
        return session.getId();
    }

    @Override
    public void write(Object message) {
        session.write(message);
    }

    @Override
    public void close() {
        session.closeOnFlush();
    }

    @Override
    public boolean isAlive() {
        return session.isActive() && session.isConnected() && !session.isClosing();
    }
}
