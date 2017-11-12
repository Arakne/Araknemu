package fr.quatrevieux.araknemu.network;

import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;

/**
 * Decorate IoHandler for adding logging
 */
final public class LoggedIoHandler implements IoHandler {
    final private IoHandler handler;
    final private Logger logger;

    public LoggedIoHandler(IoHandler handler, Logger logger) {
        this.handler = handler;
        this.logger = logger;
    }

    @Override
    public void sessionCreated(IoSession session) throws Exception {
        logger.info("New session (id: {})", session.getId());

        handler.sessionCreated(session);
    }

    @Override
    public void sessionOpened(IoSession session) throws Exception {
        handler.sessionOpened(session);
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        logger.info("Session closed (id: {})", session.getId());

        handler.sessionClosed(session);
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
        handler.sessionIdle(session, status);
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        logger.error("Uncaught exception", cause);

        handler.exceptionCaught(session, cause);
    }

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        logger.info("Recv << {}", message);

        handler.messageReceived(session, message);
    }

    @Override
    public void messageSent(IoSession session, Object message) throws Exception {
        logger.info("Send >> {}", message);

        handler.messageSent(session, message);
    }

    @Override
    public void inputClosed(IoSession session) throws Exception {
        logger.debug("Input closes (id : {})", session.getId());

        handler.inputClosed(session);
    }
}
