package fr.quatrevieux.araknemu.network.realm.out;

import fr.quatrevieux.araknemu.realm.ConnectionKey;

/**
 * Welcome message for realm server
 */
final public class HelloConnection {
    final private ConnectionKey key;

    public HelloConnection(ConnectionKey key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return "HC" + key.key();
    }
}
