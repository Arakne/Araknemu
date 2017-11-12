package fr.quatrevieux.araknemu.network.realm.out;

/**
 * Send server selection request and token
 */
final public class SelectServerPlain {
    private String ip;
    private int port;
    private String key;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return "AYK" + ip + ":" + port + ";" + key;
    }
}
