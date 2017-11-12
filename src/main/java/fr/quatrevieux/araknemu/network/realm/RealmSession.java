package fr.quatrevieux.araknemu.network.realm;

import fr.quatrevieux.araknemu.network.in.PacketParser;
import fr.quatrevieux.araknemu.network.in.PingResponse;
import fr.quatrevieux.araknemu.network.out.RPing;
import fr.quatrevieux.araknemu.realm.ConnectionKey;
import fr.quatrevieux.araknemu.realm.authentication.AuthenticationAccount;
import org.apache.mina.core.session.IoSession;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Wrap IoSession for Realm
 */
final public class RealmSession {
    final private IoSession session;
    final private boolean testing;

    public RealmSession(IoSession session) {
        this(session, false);
    }

    public RealmSession(IoSession session, boolean testing) {
        this.session = session;
        this.testing = testing;
    }

    /**
     * Get the base session
     */
    public IoSession session() {
         return session;
    }

    /**
     * Get or generate connection key
     */
    public ConnectionKey key() {
        if (!session.containsAttribute("connection_key")) {
            session.setAttribute("connection_key", new ConnectionKey());
        }

        return (ConnectionKey) session.getAttribute("connection_key");
    }

    /**
     * Get the packet parser
     * @return
     */
    public PacketParser parser() {
        return (PacketParser) session.getAttribute("packet_parser");
    }

    /**
     * Set the packet parser for the session
     * @param parser
     */
    public void setParser(PacketParser parser) {
        session.setAttributeIfAbsent("packet_parser", parser);
    }

    /**
     * Check if the packet parser is defined
     * @return
     */
    public boolean hasParser() {
        return session.containsAttribute("packet_parser");
    }

    /**
     * Attach account to the session
     */
    public void attach(AuthenticationAccount account) {
        session.setAttribute("account", account);
    }

    /**
     * Detach account from session
     */
    public void detach() {
        session.removeAttribute("account");
    }

    /**
     * Get attached account
     */
    public AuthenticationAccount account() {
        return (AuthenticationAccount) session.getAttribute("account");
    }

    /**
     * Check is the client is logged (has an attached account)
     */
    public boolean isLogged() {
        return session.containsAttribute("account");
    }

    /**
     * Write a message to the socket
     * @param message Message to send (should be stringifiable)
     */
    public void write(Object message) {
        session.write(message);
    }

    /**
     * Close the session (wait for sending messages)
     */
    public void close() {
        session.closeOnFlush();
    }

    /**
     * Check if the session is alive
     *
     * Apache mina do not check for socket state, so need to send ping request for testing socket state
     */
    public boolean isAlive() {
        if (!session.isConnected() || !session.isActive() || session.isClosing()) {
            return false;
        }

        if (testing) {
            return true;
        }

        session.write(new RPing());

        Object lock = new Object();
        session.setAttribute("ping_lock", lock);

        synchronized (lock) {
            // wait 250ms
            try {
                lock.wait(250);
            } catch (InterruptedException e) {
                close();
                return false;
            }

            session.removeAttribute("ping_lock");
        }

        // @todo check response payload
        if (!session.containsAttribute("ping_response")) {
            close();
            return false;
        }

        return true;
    }

    /**
     * Session receive ping response from client
     */
    public void onPingResponse(PingResponse response) {
        if (!session.containsAttribute("ping_lock")) {
            return;
        }

        session.setAttribute("ping_response", response);

        Object lock = session.getAttribute("ping_lock");

        synchronized (lock) {
            lock.notifyAll();
        }
    }
}
