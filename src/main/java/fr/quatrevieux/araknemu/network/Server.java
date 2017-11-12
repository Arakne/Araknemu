package fr.quatrevieux.araknemu.network;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;

/**
 * Wrap Mima acceptor
 */
final public class Server implements Closeable {
    final private IoAcceptor acceptor;

    public Server(IoAcceptor acceptor) {
        this.acceptor = acceptor;
    }

    /**
     * Auto-configure the acceptor
     *
     * @param handler The IO handle
     * @param port The bind port
     * @throws IOException When bind error occurs
     */
    public Server(IoHandler handler, int port) throws IOException {
        this(makeAcceptor(handler, port));
    }

    /**
     * Get the server acceptor
     * @return
     */
    public IoAcceptor acceptor() {
        return acceptor;
    }

    /**
     * @throws IOException
     */
    public void close() throws IOException {
        acceptor.unbind();
        acceptor.dispose();
    }

    /**
     * Create the acceptor instance
     *
     * @param handler
     * @param port
     * @return
     * @throws IOException
     */
    static private IoAcceptor makeAcceptor(IoHandler handler, int port) throws IOException {
        NioSocketAcceptor acceptor = new NioSocketAcceptor();

        acceptor.getSessionConfig().setReceiveBufferSize(2048);
        //@todo IDLE
        //acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, Constants.MAX_IDLE_TIME);
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

        acceptor.setHandler(handler);
        acceptor.bind(new InetSocketAddress(port));
        acceptor.setCloseOnDeactivation(true);

        return acceptor;
    }
}
