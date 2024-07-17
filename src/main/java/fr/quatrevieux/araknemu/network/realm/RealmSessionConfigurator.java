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

package fr.quatrevieux.araknemu.network.realm;

import fr.quatrevieux.araknemu.core.network.exception.RateLimitException;
import fr.quatrevieux.araknemu.core.network.parser.Dispatcher;
import fr.quatrevieux.araknemu.core.network.parser.Packet;
import fr.quatrevieux.araknemu.core.network.parser.PacketParser;
import fr.quatrevieux.araknemu.core.network.session.ConfigurableSession;
import fr.quatrevieux.araknemu.core.network.session.SessionConfigurator;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.MarkerManager;

/**
 * Configure session for realm
 */
public final class RealmSessionConfigurator implements SessionConfigurator.Configurator<RealmSession> {
    private final Dispatcher<RealmSession> dispatcher;
    private final PacketParser[] loginPackets;
    private final PacketParser baseParser;
    private final Logger logger;

    public RealmSessionConfigurator(Dispatcher<RealmSession> dispatcher, PacketParser[] loginPackets, PacketParser baseParser, Logger logger) {
        this.dispatcher = dispatcher;
        this.loginPackets = loginPackets;
        this.baseParser = baseParser;
        this.logger = logger;
    }

    @Override
    public void configure(ConfigurableSession inner, RealmSession session) {
        inner.addExceptionHandler(RateLimitException.class, cause -> {
            logger.error(MarkerManager.getMarker("RATE_LIMIT"), "[{}] RateLimit : close session", session);
            session.close();

            return true;
        });

        inner.addExceptionHandler(Exception.class, cause -> {
            // If an error occurs before authenticate procedure
            // The session MUST be destroyed for security reasons
            if (!session.isLogged()) {
                session.close();
            }

            return true;
        });

        inner.addReceiveMiddleware(new RealmPacketParserMiddleware(loginPackets, baseParser));
        inner.addReceiveMiddleware((packet, next) -> dispatcher.dispatch(session, (Packet) packet));
    }
}
