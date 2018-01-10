package fr.quatrevieux.araknemu.network.realm;

import fr.quatrevieux.araknemu.network.adapter.Channel;
import fr.quatrevieux.araknemu.network.adapter.Session;
import fr.quatrevieux.araknemu.network.in.PacketParser;
import fr.quatrevieux.araknemu.realm.ConnectionKey;
import fr.quatrevieux.araknemu.realm.authentication.AuthenticationAccount;

/**
 * Wrap IoSession for Realm
 */
final public class RealmSession implements Session {
    final private Channel channel;
    final private PacketParser parser;
    final private ConnectionKey key;

    private AuthenticationAccount account;

    public RealmSession(Channel channel, PacketParser parser) {
        this.channel = channel;
        this.parser  = parser;
        key = new ConnectionKey();
    }

    @Override
    public Channel channel() {
        return channel;
    }

    @Override
    public void write(Object packet) {
        channel.write(packet);
    }

    @Override
    public void close() {
        channel.close();
    }

    @Override
    public boolean isAlive() {
        return channel.isAlive();
    }

    /**
     * Get or generate connection key
     */
    public ConnectionKey key() {
        return key;
    }

    /**
     * Get the packet parser
     */
    public PacketParser parser() {
        return parser;
    }

    /**
     * Attach account to the session
     */
    public void attach(AuthenticationAccount account) {
        this.account = account;
    }

    /**
     * Detach account from session
     */
    public void detach() {
        account = null;
    }

    /**
     * Get attached account
     */
    public AuthenticationAccount account() {
        return account;
    }

    /**
     * Check is the client is logged (has an attached account)
     */
    public boolean isLogged() {
        return account != null;
    }
}
