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

package fr.quatrevieux.araknemu.network.realm;

import fr.quatrevieux.araknemu.core.network.exception.RateLimitException;
import fr.quatrevieux.araknemu.core.network.parser.Dispatcher;
import fr.quatrevieux.araknemu.core.network.parser.Packet;
import fr.quatrevieux.araknemu.core.network.parser.PacketParser;
import fr.quatrevieux.araknemu.core.network.session.ConfigurableSession;
import fr.quatrevieux.araknemu.core.network.session.SessionConfigurator;
import org.slf4j.Logger;

/**
 * Configure session for realm
 */
final public class RealmSessionConfigurator implements SessionConfigurator.Configurator<RealmSession> {
    final private Dispatcher<RealmSession> dispatcher;
    final private PacketParser[] loginPackets;
    final private PacketParser baseParser;
    final private Logger logger;

    public RealmSessionConfigurator(Dispatcher<RealmSession> dispatcher, PacketParser[] loginPackets, PacketParser baseParser, Logger logger) {
        this.dispatcher = dispatcher;
        this.loginPackets = loginPackets;
        this.baseParser = baseParser;
        this.logger = logger;
    }

    @Override
    public void configure(ConfigurableSession inner, RealmSession session) {
        inner.addExceptionHandler(RateLimitException.class, cause -> {
            logger.error("[{}] RateLimit : close session", session.channel().id());
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
