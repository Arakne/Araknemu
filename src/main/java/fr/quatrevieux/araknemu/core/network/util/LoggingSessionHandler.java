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

package fr.quatrevieux.araknemu.core.network.util;

import fr.quatrevieux.araknemu.core.network.Channel;
import fr.quatrevieux.araknemu.core.network.Session;
import fr.quatrevieux.araknemu.core.network.SessionHandler;
import fr.quatrevieux.araknemu.core.network.parser.HandlerNotFoundException;
import org.slf4j.Logger;

/**
 * Decorate IoHandler for adding logging
 */
final public class LoggingSessionHandler<S extends Session> implements SessionHandler<S> {
    final private SessionHandler<S> handler;
    final private Logger logger;

    public LoggingSessionHandler(SessionHandler<S> handler, Logger logger) {
        this.handler = handler;
        this.logger  = logger;
    }

    @Override
    public void opened(S session) throws Exception {
        logger.info("Session opened : {}", session.channel().id());

        handler.opened(session);
    }

    @Override
    public void closed(S session) throws Exception {
        logger.info("Session closed : {}", session.channel().id());

        handler.closed(session);
    }

    @Override
    public void received(S session, String packet) throws Exception {
        logger.info("Recv << {}", packet);

        handler.received(session, packet);
    }

    @Override
    public void sent(S session, Object packet) throws Exception {
        logger.info("Send >> {}", packet);

        handler.sent(session, packet);
    }

    @Override
    public void exception(S session, Throwable cause) {
        if (cause instanceof HandlerNotFoundException) {
            logger.warn(cause.getMessage());
            return;
        }

        logger.error("Uncaught exception", cause);

        if (cause.getCause() != null) {
            logger.error("Cause : {}", cause.getCause());
        }

        handler.exception(session, cause);
    }

    @Override
    public S create(Channel channel) {
        return handler.create(channel);
    }
}
