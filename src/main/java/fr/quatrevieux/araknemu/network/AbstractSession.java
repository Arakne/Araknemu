package fr.quatrevieux.araknemu.network;

import fr.quatrevieux.araknemu.network.in.PingResponse;
import fr.quatrevieux.araknemu.network.out.RPing;
import org.apache.mina.core.session.IoSession;

/**
 * Base Dofus session class
 */
abstract public class AbstractSession {
    final protected IoSession session;

    public AbstractSession(IoSession session) {
        this.session = session;
    }

    /**
     * Get the base session
     */
    public IoSession session() {
        return session;
    }

    /**
     * Check if the session is in testing mode
     */
    public boolean testing() {
        return session.containsAttribute("testing");
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

        if (testing()) {
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
