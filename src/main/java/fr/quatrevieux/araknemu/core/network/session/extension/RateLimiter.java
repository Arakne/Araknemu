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

package fr.quatrevieux.araknemu.core.network.session.extension;

import fr.quatrevieux.araknemu.core.network.InternalPacket;
import fr.quatrevieux.araknemu.core.network.exception.RateLimitException;
import fr.quatrevieux.araknemu.core.network.session.ConfigurableSession;
import fr.quatrevieux.araknemu.core.network.session.Session;
import fr.quatrevieux.araknemu.core.network.session.SessionConfigurator;

import java.util.function.Consumer;

/**
 * Limit the number of received packets per seconds
 *
 * When the limit is reached, the packet is ignored, and a {@link RateLimitException} is raised
 */
final public class RateLimiter implements ConfigurableSession.ReceivePacketMiddleware {
    final static public class Configurator<S extends Session> implements SessionConfigurator.Configurator<S> {
        final private int maxPackets;

        public Configurator(int maxPackets) {
            this.maxPackets = maxPackets;
        }

        @Override
        public void configure(ConfigurableSession inner, S session) {
            inner.addReceiveMiddleware(new RateLimiter(maxPackets));
        }
    }

    final private int maxPackets;

    private long lastTime;
    private int packetsCount;

    public RateLimiter(int maxPackets) {
        this.maxPackets = maxPackets;
    }

    @Override
    public void handlePacket(Object packet, Consumer<Object> next) {
        // Internal packets must not be blocked
        if (packet instanceof InternalPacket) {
            next.accept(packet);
            return;
        }

        final long currentTime = System.currentTimeMillis() / 1000L;

        if (lastTime != currentTime) {
            lastTime = currentTime;
            packetsCount = 0;

            next.accept(packet);
            return;
        }

        if (++packetsCount >= maxPackets) {
            throw new RateLimitException();
        }

        next.accept(packet);
    }
}
