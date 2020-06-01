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

package fr.quatrevieux.araknemu.core.network.session.extension;

import fr.quatrevieux.araknemu.core.network.SessionClosed;
import fr.quatrevieux.araknemu.core.network.SessionCreated;
import fr.quatrevieux.araknemu.core.network.exception.HandlingException;
import fr.quatrevieux.araknemu.core.network.parser.HandlerNotFoundException;
import fr.quatrevieux.araknemu.core.network.session.ConfigurableSession;
import fr.quatrevieux.araknemu.core.network.session.Session;
import fr.quatrevieux.araknemu.core.network.session.SessionConfigurator;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.function.Consumer;

/**
 * Add loggers to a session
 */
final public class SessionLogger implements ConfigurableSession.ExceptionHandler<Throwable>, ConfigurableSession.ReceivePacketMiddleware, ConfigurableSession.SendPacketTransformer {
    static public class Configurator<S extends Session> implements SessionConfigurator.Configurator<S> {
        final private Logger logger;

        public Configurator(Logger logger) {
            this.logger = logger;
        }

        @Override
        public void configure(ConfigurableSession inner, Session session) {
            SessionLogger sessionLogger = new SessionLogger(session, logger);

            inner.addExceptionHandler(sessionLogger);
            inner.addSendTransformer(sessionLogger);
            inner.addReceiveMiddleware(sessionLogger);
        }
    }

    final static private Marker RECEIVED_MARKER = MarkerManager.getMarker("RECEIVED");
    final static private Marker SENT_MARKER = MarkerManager.getMarker("SENT");
    final static private Marker SESSION_MARKER = MarkerManager.getMarker("SESSION");
    final static private Marker NETWORK_ERROR_MARKER = MarkerManager.getMarker("NETWORK_ERROR");

    final private Session session;
    final private Logger logger;

    public SessionLogger(Session session, Logger logger) {
        this.session = session;
        this.logger = logger;
    }

    @Override
    public Class<Throwable> type() {
        return Throwable.class;
    }

    @Override
    public boolean handleException(Throwable cause) {
        if (cause instanceof HandlingException) {
            return true;
        }

        if (cause instanceof HandlerNotFoundException) {
            logger.warn(NETWORK_ERROR_MARKER, cause.getMessage());

            return false;
        }

        logger.error(NETWORK_ERROR_MARKER, "[{}] Uncaught exception", session, cause);

        if (cause.getCause() != null) {
            logger.error(NETWORK_ERROR_MARKER, "[{}] Cause : {}", session, cause.getCause());
        }

        return true;
    }

    @Override
    public void handlePacket(Object packet, Consumer<Object> next) {
        if (packet instanceof SessionCreated) {
            logger.debug(SESSION_MARKER, "[{}] Session created", session);
        } else if (packet instanceof SessionClosed) {
            logger.debug(SESSION_MARKER, "[{}] Session closed", session);
        } else {
            logger.debug(RECEIVED_MARKER, "[{}] Recv << {}", session, packet);
        }

        next.accept(packet);
    }

    @Override
    public Object transformPacket(Object packet) {
        logger.debug(SENT_MARKER, "[{}] Send >> {}", session, packet);

        return packet;
    }
}
