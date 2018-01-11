package fr.quatrevieux.araknemu.network.adapter.util;

import fr.quatrevieux.araknemu.network.adapter.Channel;
import fr.quatrevieux.araknemu.network.adapter.Session;
import fr.quatrevieux.araknemu.network.adapter.SessionHandler;
import fr.quatrevieux.araknemu.network.in.HandlerNotFoundException;
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
