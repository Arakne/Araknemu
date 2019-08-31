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

import fr.quatrevieux.araknemu.network.adapter.Server;
import fr.quatrevieux.araknemu.network.adapter.SessionHandler;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;

/**
 * Wrap Mima acceptor
 */
final public class MinaServer implements Server {
    final private SessionHandler handler;
    final private int port;

    private NioSocketAcceptor acceptor;

    /**
     * Auto-configure the acceptor
     *
     * @param handler The IO handle
     * @param port The bind port
     */
    public MinaServer(SessionHandler handler, int port) {
        this.handler = handler;
        this.port = port;
    }

    /**
     * Get the server acceptor
     */
    public IoAcceptor acceptor() {
        return acceptor;
    }

    @Override
    public void stop() throws IOException {
        acceptor.unbind();
        acceptor.dispose();
    }

    @Override
    public void start() throws IOException {
        acceptor = new NioSocketAcceptor(
            Runtime.getRuntime().availableProcessors()
        );

        acceptor.getSessionConfig().setReceiveBufferSize(512);
        acceptor.getSessionConfig().setKeepAlive(true);
        acceptor.getSessionConfig().setBothIdleTime(10);

        acceptor.getFilterChain().addLast(
            "codec",
            new ProtocolCodecFilter(
                new TextLineCodecFactory(
                    Charset.forName("UTF-8"),
                    "\000",
                    "\n\000"
                )
            )
        );

        acceptor.setReuseAddress(true);
        acceptor.setHandler(new IoHandlerAdapter(handler));
        acceptor.bind(new InetSocketAddress(port));
        acceptor.setCloseOnDeactivation(true);
    }
}
