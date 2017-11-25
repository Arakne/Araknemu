package fr.quatrevieux.araknemu.network.realm;

import fr.quatrevieux.araknemu.network.AbstractSession;
import fr.quatrevieux.araknemu.network.in.PacketParser;
import fr.quatrevieux.araknemu.realm.ConnectionKey;
import fr.quatrevieux.araknemu.realm.authentication.AuthenticationAccount;
import org.apache.mina.core.session.IoSession;

/**
 * Wrap IoSession for Realm
 */
final public class RealmSession extends AbstractSession {
    public RealmSession(IoSession session) {
        super(session);
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
}
