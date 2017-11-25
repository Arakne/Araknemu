package fr.quatrevieux.araknemu.network.realm.out;

/**
 * Send server selection request and token
 */
final public class SelectServerPlain {
    final private String ip;
    final private int port;
    final private String token;

    public SelectServerPlain(String ip, int port, String token) {
        this.ip = ip;
        this.port = port;
        this.token = token;
    }

    @Override
    public String toString() {
        return "AYK" + ip + ":" + port + ";" + token;
    }
}
