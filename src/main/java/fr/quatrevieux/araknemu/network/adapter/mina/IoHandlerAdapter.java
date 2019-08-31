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

import fr.quatrevieux.araknemu.network.adapter.Session;
import fr.quatrevieux.araknemu.network.adapter.SessionHandler;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

/**
 * Adapt SessionHandler to apache mine IoHandler
 *
 * @param <S> The session type
 */
final public class IoHandlerAdapter<S extends Session> implements IoHandler {
    final private SessionHandler<S> handler;

    public IoHandlerAdapter(SessionHandler<S> handler) {
        this.handler = handler;
    }

    @Override
    public void sessionCreated(IoSession session) throws Exception {}

    @Override
    public void sessionOpened(IoSession session) throws Exception {
        S araknemuSession = handler.create(new ChannelAdapter(session));
        session.setAttribute("session", araknemuSession);

        handler.opened(araknemuSession);
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        handler.closed(
            (S) session.getAttribute("session")
        );

        session.removeAttribute("session");
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status) throws Exception {}

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {}

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        handler.received(
            (S) session.getAttribute("session"),
            message.toString()
        );
    }

    @Override
    public void messageSent(IoSession session, Object message) throws Exception {
        handler.sent(
            (S) session.getAttribute("session"),
            message
        );
    }

    @Override
    public void inputClosed(IoSession session) throws Exception {}
}
